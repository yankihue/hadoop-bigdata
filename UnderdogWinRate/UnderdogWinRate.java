import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class UnderdogWinRate {

    public static class UnderdogWinRateMapper extends Mapper<LongWritable, Text, Text, IntWritable> {
        private final static IntWritable one = new IntWritable(1);
        private final static IntWritable zero = new IntWritable(0);
        private Text word = new Text("UnderdogWin");

        public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            String[] tokens = value.toString().split(",");

            if (tokens.length > 8 && !tokens[6].equals("WhiteElo") && !tokens[7].equals("BlackElo")) {
                double whiteElo = Double.parseDouble(tokens[6]);
                double blackElo = Double.parseDouble(tokens[7]);
                String result = tokens[3];

                if ((whiteElo < blackElo && result.equals("1-0")) || (whiteElo > blackElo && result.equals("0-1"))) {
                    context.write(word, one);
                } else {
                    context.write(word, zero);
                }
            }
        }
    }

    public static class UnderdogWinRateReducer extends Reducer<Text, IntWritable, Text, DoubleWritable> {
        public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
            int totalGames = 0;
            int underdogWins = 0;

            for (IntWritable value : values) {
                totalGames++;
                underdogWins += value.get();
            }

            double winRate = (double) underdogWins / totalGames;
            context.write(key, new DoubleWritable(winRate));
        }
    }

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "Underdog Win Rate");
        job.setJarByClass(UnderdogWinRate.class);
        job.setMapperClass(UnderdogWinRateMapper.class);
        
        job.setReducerClass(UnderdogWinRateReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}

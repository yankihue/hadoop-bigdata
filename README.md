# Hadoop multi-node cluster big data analysis examples 
### Hadoop multi-node setup on VMs
[Link](https://medium.com/@jootorres_11979/how-to-set-up-a-hadoop-3-2-1-multi-node-cluster-on-ubuntu-18-04-2-nodes-567ca44a3b12)

### Dataset used for written example jobs
[Link](https://www.kaggle.com/datasets/arevel/chess-games)

### Steps to run scripts 
After setting up hadoop, start hdfs with
```bash
start-dfs.sh
```
Check that the namenodes and datanodes are working as intended. (Check both in master and worker machines)
```bash
jps
```
You then need to move the data to hdfs to start working on it. First, we need to create the user directory inside hdfs to be able to move files to it.

```bash
hdfs dfs -mkdir -p /user/hadoopuser
```
Make sure when you download the dataset that `hadoopuser` has access to it rather than the default user on your machine. So you should switch to hadoopuser from GNOME and download the dataset from there.

Afterwards, you can move the database to hdfs. (Change the path of your .csv file accordingly)
```bash
hdfs dfs -put /home/hadoopuser/Desktop/chess_games.csv /input
```

These are the steps you need to follow to run any given java job on your cluster. You can just download the .jar file of any job from this repo and only run the `hadoop jar ...` step but the first 2 steps are what you need to compile your own custom jobs should you write any.

```bash
javac -classpath $(hadoop classpath) LongGameBlackWinRate.java 

jar cf LongGameBlackWinRate.jar LongGameBlackWinRate*.class

hadoop jar LongGameBlackWinRate.jar LongGameBlackWinRate /input /output
```


### Example Jobs
- [Underdog Win Rate](https://github.com/yankihue/hadoop-bigdata/blob/main/UnderdogWinRate/UnderdogWinRate.java): Calculates the win rate of underdogs in the dataset (an underdog is a player with a lower ELO rating than their opponent). Result:  33% (0.3305420684557871)
- [Long Game Black Win Rate](https://github.com/yankihue/hadoop-bigdata/blob/main/LongGameBlackWinRate/LongGameBlackWinRate.java): Calculates the win rate of black when the
number of moves in a game is more than 100. Result 47% (0.4770832870257174)
- [Popular Opening High Elo](https://github.com/yankihue/hadoop-bigdata/blob/main/PopularOpeningHighElo/PopularOpeningHighElo.java): Returns the most popular opening when both players have an ELO rating greater than 1900. Result: "Modern Defense"

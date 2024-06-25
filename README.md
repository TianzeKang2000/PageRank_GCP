Step 1: Set Up PySpark on GCP
Create a GCP Account: If you don't have one already, create a GCP account.
Create a Dataproc Cluster:
Go to GCP Console: Open the GCP Console and navigate to Dataproc.
Create Cluster: Click on "Create Cluster", select the appropriate configuration (e.g., number of nodes, machine type, etc.).
Step 2: Write the Scala Code
Write a Scala program to implement the PageRank algorithm using Spark's GraphX library. Create a new Scala file, for example, PageRank.scala.

Example Scala Code:
scala
Copy code
import org.apache.spark.sql.SparkSession
import org.apache.spark.graphx._
import org.apache.spark.rdd.RDD

object PageRank {
  def main(args: Array[String]): Unit = {
    // Initialize SparkSession
    val spark = SparkSession.builder
      .appName("PageRank")
      .getOrCreate()

    val sc = spark.sparkContext

    // Load graph data
    val edges: RDD[Edge[Double]] = sc.textFile("gs://your-bucket/Sample_Graph_Data.csv")
      .map { line =>
        val fields = line.split(",")
        Edge(fields(0).toLong, fields(1).toLong, 1.0)
      }

    // Create Graph
    val graph = Graph.fromEdges(edges, 1.0)

    // Run PageRank algorithm
    val ranks = graph.pageRank(0.15).vertices

    // Display results
    ranks.collect().foreach { case (id, rank) =>
      println(s"Vertex $id has rank $rank.")
    }

    // Save results
    ranks.map { case (id, rank) => s"$id,$rank" }
      .saveAsTextFile("gs://your-bucket/pagerank-results")

    // Stop SparkSession
    spark.stop()
  }
}
Step 3: Compile and Upload the Scala Code
Compile the Scala Code:

Use SBT (Scala Build Tool) to compile your Scala code. Create a build.sbt file with the following content:
scala
name := "PageRank"

version := "0.1"

scalaVersion := "2.12.8"

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % "3.0.0",
  "org.apache.spark" %% "spark-graphx" % "3.0.0",
  "org.apache.spark" %% "spark-sql" % "3.0.0"
)
Run the following commands to compile your code:
sh
Copy code
sbt package
Upload the Compiled JAR to GCS:

Upload the compiled JAR file to your Google Cloud Storage bucket:
sh
Copy code
gsutil cp target/scala-2.12/pagerank_2.12-0.1.jar gs://your-bucket/
Step 4: Submit the Spark Job
Submit the Job to Dataproc:
Use the following command to submit the job to your Dataproc cluster:

gcloud dataproc jobs submit spark --cluster your-cluster-name \
  --class PageRank --jars gs://your-bucket/pagerank_2.12-0.1.jar \
  -- gs://your-bucket/Sample_Graph_Data.csv gs://your-bucket/pagerank-results
This command tells Dataproc to run the PageRank class from the uploaded JAR file on your specified cluster, using the input data from gs://your-bucket/Sample_Graph_Data.csv and saving the results to gs://your-bucket/pagerank-results.

Summary
Set Up PySpark on GCP: Create a Dataproc cluster.
Write Scala Code: Implement the PageRank algorithm using Scala and GraphX.
Compile and Upload: Compile the Scala code using SBT and upload the JAR file to Google Cloud Storage.
Submit Job: Submit the compiled JAR file as a Spark job on your Dataproc cluster.
If you encounter any issues or need further assistance, please let me know!

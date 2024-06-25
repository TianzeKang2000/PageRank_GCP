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

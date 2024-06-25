from pyspark.sql import SparkSession
from pyspark.sql.functions import *
from pyspark.sql.types import *
from graphframes import GraphFrame

# Initialize Spark session
spark = SparkSession.builder.appName("PageRank").getOrCreate()

# Load data
edges = spark.read.csv('file:///home/kang20000627/Sample_Graph_Data.csv', header=True, inferSchema=True)

# Create the graph
vertices = edges.selectExpr("src as id").union(edges.selectExpr("dst as id")).distinct()
g = GraphFrame(vertices, edges)

# Run PageRank
results = g.pageRank(resetProbability=0.15, maxIter=10)

# Display the results
results.vertices.select("id", "pagerank").show()

# Save the results
results.vertices.select("id", "pagerank").write.csv('file:///home/kang20000627/pagerank-results.csv', header=True)

# Stop the Spark session
spark.stop()

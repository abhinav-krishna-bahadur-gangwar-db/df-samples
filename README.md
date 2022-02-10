# Data Flow Sample Pipelines

## SimpleFilterPipeline
A Simple Pipeline to demonstrate a DataFlow Filter

Use following command to create a template

```
mvn compile exec:java -Dexec.mainClass=com.beam.examples.SimpleFilterPipeline -Dexec.cleanupDaemonThreads=false -Dexec.args="--runner=DataflowRunner --project=golden-veld-340908 --stagingLocation=gs://golden-veld-340908-dist/staging --tempLocation=gs://golden-veld-340908-temp/working --templateLocation=gs://golden-veld-340908-dist/templates/simple_filter_template --region=us-central1"
```
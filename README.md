# Data Flow Sample Pipelines

## SimpleFilterPipeline
A Simple Pipeline to demonstrate a DataFlow Filter

Use following command to create a template
```
mvn compile exec:java \
 -Dexec.mainClass=com.beam.examples.MilestoneCountExample \
 -Dexec.cleanupDaemonThreads=false \
 -Dexec.args="--runner=DataflowRunner \
              --project=PROJECT_ID \
              --stagingLocation=gs://BUCKET_NAME/staging \
              --templateLocation=gs://BUCKET_NAME/templates/milestone \
              --region=uscentral1"
```
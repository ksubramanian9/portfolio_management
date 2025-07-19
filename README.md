# portfolio_management

Investment Portfolio Management

## Building
This project uses [sbt](https://www.scala-sbt.org/) to build the microservices. Run `sbt compile` or `sbt test` from the repository root.

### Building with Docker
The repository includes a Dockerfile that builds the service, runs the tests,
and packages the resulting JAR. Use the following commands to create and run the
container:

```bash
# Build the image (runs the tests during the build stage)
docker build -f docker/portfolio-management-service/Dockerfile -t portfolio-management .

# Start the container with the packaged application
docker run --rm portfolio-management
```

Additional documentation can be found in the [docs/](docs/) directory.

# Tracing Data Consolidation Tool

[![spe-uob](https://circleci.com/gh/spe-uob/Tracing-Data-Consolidation-Tool.svg?style=shield)](https://app.circleci.com/pipelines/github/spe-uob/Tracing-Data-Consolidation-Tool)

## Description

A consolidation tool to pull together the data spreadsheets, providing a consistent, clean set of data, with any duplicates identified and removed.
The tool should have the ability to interrogate data in order to draw off specific queries and to run management information reports.

Key considerations:
- The tool would need to gather the data sources from the Excel spreadsheets, with no requirement to interact with the source data systems.
- The tool would be required to cope with large volumes
of data and to run any reports quickly.

Licensed under the MIT license (see `LICENSE.md`).

## Usage

Install [docker](https://docs.docker.com/get-docker/).

### Quick Start

In the root directory, run the following on command line:
```
docker-compose up -d
```

The `-d` flag means 'detached' (runs in background).
This will build the necessary containers if they are not present already (i.e. first time running). (To rebuild containers, see **Development** section below.)

Once finished with using the application, stop and remove all containers with:
```
docker-compose down
```

### Development

To build the containers, run:
```
docker-compose build
```

Alternatively, to rebuild and run all containers:
```
docker-compose up --build -d
```

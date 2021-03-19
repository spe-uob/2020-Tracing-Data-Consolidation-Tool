# Tracing Data Consolidation Tool

[![spe-uob](https://circleci.com/gh/spe-uob/Tracing-Data-Consolidation-Tool.svg?style=shield)](https://app.circleci.com/pipelines/github/spe-uob/Tracing-Data-Consolidation-Tool)

A consolidation tool to pull together the data spreadsheets,
providing a consistent, clean set of data, with any duplicates identified and removed. 
The tool should have the ability to interrogate data in order to draw off specific queries and to run management information reports.
Key considerations; 
- The tool would need to gather the data sources from the Excel spreadsheets, with no requirement to interact with the source data systems. 
- The tool would be required to cope with large volumes
of data and to run any reports quickly. 

Licensed under the MIT license (see `LICENSE.md`).

# Building and Running

Prerequesites: [maven](https://maven.apache.org/download.cgi), [node.js](https://nodejs.org/en/download/).

1. Navigate to `com.DataConsolidation`.
2. Inside `src/main/resources/`, create folders named `UploadedFiles` and `ProcessedFiles` if they don't already exist.
3. Run the backend via IntelliJ (you can also do this from the CLI with `mvn spring-boot:run`).
4. Navigate to `src/main/frontend`. Run with `npm start`. If you get an error, try `npm install` and run again.

Step 3 should open a tab in your browser, but if not you can visit [http://localhost:3000](http://localhost:3000).

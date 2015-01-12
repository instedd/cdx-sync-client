cdx-sync-client
===============

Welcome to cdx-sync-client. This repository contains library code to develop rsync-based file synchronization clients. 

This repository just contains client code, server code can be found [here](https://github.com/instedd/cdx-sync-server). 

# Running a sync app

This client contains also a simple application that does sync'ing. It is builded automatically during the package phase. Run it this way:

```
java -jar target/cdxsync src/test/resources/cdxsync.properties
```


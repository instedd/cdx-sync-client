CDX Client
============

Welcome to cdx-sync-client. This repository contains library code to develop rsync-based file synchronization clients.

This repository just contains client code, server code can be found [here](https://github.com/instedd/cdx-sync-server).

# Prerequesites

Download and install in your local maven cache the [rsync-java-client](https://github.com/instedd/rsync_java_client) by cloning the project and running `mvn install`.

# Building from source

```bash
$ ./gradlew build      # get deps, builds the project
$ ./gradlew eclipse    # lets you import into Eclipse
$ ./gradlew installer  # generates the installer
```


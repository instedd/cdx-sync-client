CDX Client
============

Welcome to cdx-sync-client. This repository contains library code to develop rsync-based file synchronization clients.

This repository just contains client code, server code can be found [here](https://github.com/instedd/cdx-sync-server).

## Submodules

This project uses git submodules. To get the full repository you have to run this commands from the project's root directory:

```bash
$ git submodule init
$ git submodule update
```

# Building from source

```bash
$ ./gradlew build      # get deps, builds the project
$ ./gradlew eclipse    # lets you import into Eclipse
$ ./gradlew installer  # generates the installer
```

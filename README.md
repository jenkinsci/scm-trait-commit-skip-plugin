# Commit skip SCM filters

This repository contains a collection of traits for several Jenkins branch source plugins.

It provides filters for both pull requests and/or branches on jobs created from these plugins

- [GitHub Branch Source](https://plugins.jenkins.io/github-branch-source/)
- [Bitbucket Branch Source](https://plugins.jenkins.io/cloudbees-bitbucket-branch-source/)

The filtering will be performed, applying it whether it:

- The last commit message contains "[skip ci]" or "[ci skip]". The check is case-insensitive.
- The last commit message matches a pattern.
- The last commit author matches a pattern.

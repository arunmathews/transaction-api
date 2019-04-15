# transaction-api #

This service provides apis to create and query transactions

# Design #

There are three main layers. Service -> Handler -> API.

Service - Uses a web framework to accept requests. The framework could be
Scalatra, Finch etc. We are currently using Scalatra.

Handler - Business logic layer. Self contained with no external
dependencies.

API - Specifies the external dependencies that is required by the
handler. We could swap out different implementations of the API as
needed. These dependencies could be databases, other services etc.

# Scalatra #
[Scalatra home](http://scalatra.org)

# Initial Setup #
1. Install Java8
1. Install sbt
1. Install rbenv and a newer version of Ruby than the current Mac default
   * `brew install rbenv`
   * `rbenv install 2.4.1`
1. Create override/docker_desktop.env file with values:
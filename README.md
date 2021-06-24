# Keeper Android Client


This is an old app, that I refator to work on new APIs

this is a WIP.

# Purpose

In a case where a driver must take a predeterminated route, this app log the travel and also give feedback to driver if a condition is make, like to be careful on some sector, if max speed is reached.

This app take a defined .kml route and act as a copilot to prevent that driver pass max speed, take precautions and log route taken.

The travel information is saved to a local sqlite database that wherever is network connection, automatically upload the data to a central server where is permanently stored and report can be generated.

All data can be checked by a web app that take this data from the central storage.



# EmoRecord v1.0

## Overview

This is a modification of EmoStateLog (included in the Emotiv SDK), which interfaces with the Emotiv EPOC BCI. EmoRecord allows users to collect all emotional data recorded by the Emotiv Control Panel. Selected emotional data is written to a csv placed in the project's root directory.

## Instructions

To collect data the program piggybacks off the Emotiv Control Panel. Before running the Java application make sure that the Emotiv Control Panel is running, and that in the toolbar, under Connect, To EmoEngine is selected. This ensures that data collected syncs correctly with already established user profiles.
This project should be downloaded and added as a project in Eclipse.

## Features to be added/changed

1. GUI improvements
2. Ability to choose save location/filename
3. Timer
4. Real-time plotting
5. Collect program into one jar

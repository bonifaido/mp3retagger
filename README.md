mp3retagger
===========

Creates ID3 tags for your mp3 files based on their filenames. Simply splits the filename at the fist '-' and considers the first part as 'Artist' and the second parts as 'Title', so name your files accordingly.

## Build
You will need Maven to build the project.

    mvn package

## Usage

Re-tag all your mp3 files under a folder (not recursive):

    java -jar target/mp3retagger.jar ~/MyMusic/*.mp3

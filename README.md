# QID Generator

## Overview

This project was created, in a PPDP context, in order to generate 
quasi-identifiers (QID) for professors contained in RDF graphs generated 
thanks to the LUBM benchmark (http://swat.cse.lehigh.edu/projects/lubm/).

We consider 3 types of QIDs:
* An **age** which is between 20 and 100.
* A **sex**: male or female
* A **zipcode** bounded between 2 values provided by the user: a starting value
 and a range. For instance, with a starting value equal to 80000 and a range
  equal to 300, each professor in the dataset will be associated 
  with a zipcode value in [80000, 80300[
  
The values for each QID are distributed evenly among the professors.
The program produces 3 .ttl files, one for each QID.

## Execution

A .jar file is provided for an easier execution. Several arguments may be 
provided by the user:

* -d [directory1] [directory2] ...  :   Loads the files contained in the
directories into the model.
* -f [file1] [file2] …   :   Loads the files into the model.
* -tdb [dataset]    :    Loads a Jena TDB dataset into the model.
* -zipcode [start] [range]   :  Sets the starting value as well as the range
for the generation of zipcodes.
* -out [directory]   :    Sets the path of the directory in which the files
are to be created.

**Execution example**:  
> java -jar QID_generator.jar -d LUBM_3_univ/ -zipcode 80000 100 -out qids/

This command loads the files contained in directory *LUBM_3_univ/*,
the zipcode that will be assigned will all be in the range [80000, 80100[ and
the qid files will be generated in directory *qids*



# ModelGenerator
This is the implementation of the technique I proposed in my Ph.D. thesis.

The proposed technique seeks to generate a UML activity diagram of a new use case, by:
1. Retrieving, in a UML model repository, the most similar existing use case to the new use case
2. Generating an activity diagram for the new use case by automatically adapting the activity diagram associated with the retrieved similar use case

For this, I have proposed the following components:
1. A parser (RDFizer) to provide an ontological representation of UML models
1. A repository which stores the semantic representation of the existing UML models
1. A semantic similarity metric for measuring similarity of UML use cases, considering different elements present in a UML use case diagram
1. An algorithm for automatically annotating an existing activity diagram with the concepts in the associated UML class diagram, e.g. class and attribute names
1. An algorithm for automatically adapting an annotated activity diagram of an existing use case for a new use case

More details about my proposed technique can be found in my related papers:
1. [A semi-automated approach to adapt activity diagrams for new use cases](https://www.sciencedirect.com/science/article/abs/pii/S0950584914001463)
1. [A semantic web enabled approach to reuse functional requirements models in web engineering](https://link.springer.com/article/10.1007/s10515-014-0144-4)

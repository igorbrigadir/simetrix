simetrix
========

Mavenized Fork of SIMetrix by Annie Louis, from http://homepages.inf.ed.ac.uk/alouis/IEval2.html


SIMetrix: Summary Input similarity Metrics (Version 1)
========

This tool is built to perform summary content evaluation by comparing a generated summary with the source document for which it was produced. Since summaries are expected to be surrogates of the input, high similarity with the source would be indicative of good quality summaries and vice versa.

This package contains code to obtain various input-summary similarily metrics that were compared in our work described in the following papers.

* A. Louis and A. Nenkova, Automatically Evaluating Content Selection in Summarization without Human Models, EMNLP 2009.
* A. and A. Nenkova, Predicting Summary Quality using Limited Human Input, TAC 2009.
* A. Louis and A. Nenkova, Summary Evaluation without Human Models, Poster, TAC 2008.

An information-theoretic measure, Jensen Shannon divergence between vocabulary distributions of the input and summary texts was found to produce the best predictions of summary quality. System scores produced by this metric obtain correlations with pyramid scores in the range of 0.89 (TAC 2008 data) and 0.74 (TAC 2009). More details about the performance of various features can be found in the papers above.

Original Links:
=========
[http://homepages.inf.ed.ac.uk/alouis/software/ieval/SIMetrix-v1.tar.gz](Download SIMetrix) Version 1 (source and examples)
[http://homepages.inf.ed.ac.uk/alouis/software/ieval/README.txt](View the README file) for information about how to set up an evaluation with the tool.

package edu.upenn.seas.simetrix;

import java.util.LinkedList;

/**
 * Contains code to compute all features for input-based evaluation except cosine overlap (CorpusBasedUtilities.java).
 * @author Annie Louis
 */
public class EvalFeatures {
  /**
   * Computes the Jensen Shannon divergence between 2 given vocabulary distributions.
   * No smoothing is done in this version.
   */
  public double getJSDivergence(vocabDist distA, vocabDist distB) {
    double probA, probB, jsd = 0.0;

    LinkedList<String> complete = new LinkedList<String>();
    complete.addAll(distA.vocabWords);

    for (int c = 0; c < distB.vocabWords.size(); c++) {
      if (!complete.contains(distB.vocabWords.get(c))) {
        complete.add(distB.vocabWords.get(c));
      }
    }
    for (int i = 0; i < complete.size(); i++) {
      int indA = distA.vocabWords.indexOf(complete.get(i));
      if (indA == -1) {
        probA = 0;
      } else {
        probA = distA.vocabFreq.get(indA) / ((double) distA.numTokens);
      }

      int indB = distB.vocabWords.indexOf(complete.get(i));
      if (indB == -1) {
        probB = 0;
      } else {
        probB = distB.vocabFreq.get(indB) / ((double) distB.numTokens);
      }

      double part1, part2;
      if (probA == 0) {
        part1 = 0;
      } else {
        part1 = probA * Math.log(probA / ((probA / 2) + (probB / 2)));
      }
      if (probB == 0) {
        part2 = 0;
      } else {
        part2 = probB * Math.log(probB / ((probB / 2) + (probA / 2)));
      }
      jsd += (part1 + part2) / 2;
    }
    return jsd;
  }


  /**
   * Computes the Jensen Shannon divergence between 2 given vocabulary distributions.
   * Lidstone smoothing is done in this version.
   * Takes 2 more arguments related to smoothing:
   * <br> gamma - the real number estimate to be added to the count for each vocabulary item (default 0.005)
   * <br> bins - number of sample values that can be generated by the experiment behind the probability distribution (1.5 * vocabulary size of input)
   */
  public double getSmoothedJSDivergence(vocabDist distA, vocabDist distB, double gamma, double bins) {
    double countA, probA, countB, probB, jsd = 0.0;

    LinkedList<String> complete = new LinkedList<String>();

    complete.addAll(distA.vocabWords);
    for (int c = 0; c < distB.vocabWords.size(); c++) {
      if (!complete.contains(distB.vocabWords.get(c))) {
        complete.add(distB.vocabWords.get(c));
      }
    }


    for (int i = 0; i < complete.size(); i++) {
      int indA = distA.vocabWords.indexOf(complete.get(i));
      if (indA == -1) {
        countA = gamma;
      } else {
        countA = ((double) (distA.vocabFreq.get(indA))) + gamma;
      }
      probA = countA / ((distA.numTokens) + (gamma * bins));

      int indB = distB.vocabWords.indexOf(complete.get(i));
      if (indB == -1) {
        countB = gamma;
      } else {
        countB = ((double) distB.vocabFreq.get(indB)) + gamma;
      }
      probB = countB / ((distB.numTokens) + (gamma * bins));

      double part1, part2;
      if (probA == 0) {
        part1 = 0;
      } else {
        part1 = probA * Math.log(probA / ((probA / 2) + (probB / 2)));
      }
      if (probB == 0) {
        part2 = 0;
      } else {
        part2 = probB * Math.log(probB / ((probB / 2) + (probA / 2)));
      }
      jsd += (part1 + part2) / 2;
    }
    return jsd;
  }

  /**
   * Computes the Kullback Leibler divergence between 2 given vocabulary distributions.
   * Lidstone smoothing is done in this version. Takes 2 more arguments related to smoothing.
   * <br> gamma - the real number estimate to be added to the count for each vocabulary item
   * <br> bins - number of sample values that can be generated by the experiment behind the probability distribution
   * <br><br> KL divergence is not symmetric. Hence the function returns an array of two values--the first is KL(A||B) and the second KL(B||A) where
   * A and B are two vocabulary distributions provided.
   */
  public double[] getKLdivergenceSmoothed(vocabDist distA, vocabDist distB, double gamma, double bins) {
    double divDistADistB = 0, divDistBDistA = 0;
    LinkedList<String> complete = new LinkedList<String>();
    complete.addAll(distA.vocabWords);
    for (int i = 0; i < distB.vocabWords.size(); i++) {
      if (!complete.contains(distB.vocabWords.get(i))) {
        complete.add(distB.vocabWords.get(i));
      }
    }
    for (int i = 0; i < complete.size(); i++) {
      int indA = distA.vocabWords.indexOf(complete.get(i));
      int indB = distB.vocabWords.indexOf(complete.get(i));
      double countA, countB;
      if (indA == -1) {
        countA = gamma;
      } else {
        countA = ((double) distA.vocabFreq.get(indA)) + gamma;
      }
      if (indB == -1) {
        countB = gamma;
      } else {
        countB = ((double) distB.vocabFreq.get(indB)) + gamma;
      }
      double probA = countA / ((distA.numTokens) + (gamma * bins));
      double probB = countB / ((distB.numTokens) + (gamma * bins));
      double probA_B = probA / probB;
      double probB_A = probB / probA;
      divDistADistB += probA * Math.log(probA_B);
      divDistBDistA += probB * Math.log(probB_A);
    }
    if ((divDistADistB < 0) || (divDistBDistA < 0)) {
      System.out.println(" negative div = " + divDistADistB + "," + divDistBDistA);
    }
    double[] KLdiv = new double[2];
    KLdiv[0] = divDistADistB;
    KLdiv[1] = divDistBDistA;
    return KLdiv;
  }


  /**
   * Given a list of topic words and a vocabulary distribution, this function computes the percentage of tokens
   * from the vocabulary distribution that are topic words.
   */
  public double getPercentTokensThatIsSignTerms(LinkedList<String> topicWordList, vocabDist dist) {
    int count = 0;
    for (int i = 0; i < topicWordList.size(); i++) {
      String signTerm = topicWordList.get(i);
      int present = dist.vocabWords.indexOf(signTerm);
      if (present != -1) {
        count += dist.vocabFreq.get(present);
      }
    }
    if (count == 0) {
      return 0;
    }
    double percentTokens = ((double) count) / dist.numTokens;
    return (percentTokens);
  }

  /**
   * Given a list of topic words and a vocabulary distribution, this function computes what fraction of topic
   * words in the list are also present in the given vocabulary distribution.
   */
  public double getPercentTopicWordsCoveredByGivenDist(LinkedList<String> topicWordList, vocabDist dist) {
    int count = 0;
    for (int i = 0; i < topicWordList.size(); i++) {
      String signTerm = topicWordList.get(i);
      int present = dist.vocabWords.indexOf(signTerm);
      if (present != -1) {
        count++;
      }
    }
    if (count == 0) {
      return 0;
    }
    double percentCovered = ((double) count) / topicWordList.size();
    return (percentCovered);
  }

  /**
   * This function takes two vocabulary distributions. The emission probabilities for each word is computed from the
   * emission distribution. The unigram likelihood of a new distribution (newDist) under this set of emission probabilities
   * is returned.
   */
  public double getUnigramProbability(vocabDist emissionDist, vocabDist newDist) {
    double probOfNewDist = 0.0;
    for (int i = 0; i < newDist.vocabWords.size(); i++) {
      String word = newDist.vocabWords.get(i);
      int emitIndex = emissionDist.vocabWords.indexOf(word);
      int wordFreqInEmissionDist;
      if (emitIndex == -1) {
        continue;
      } else {
        wordFreqInEmissionDist = emissionDist.vocabFreq.get(emitIndex);
      }
      double wordEmissionProbability = ((double) wordFreqInEmissionDist) / emissionDist.numTokens;
      int wordFreqInNewDist = newDist.vocabFreq.get(i);
      probOfNewDist += wordFreqInNewDist * Math.log10(wordEmissionProbability);
    }
    return probOfNewDist;
  }

  /**
   * This function takes two vocabulary distributions. The emission probabilities for each word is computed from the
   * emission distribution. The multinomial likelihood of a new distribution (new Dist) under this set of emission probabilities
   * is returned.
   */
  public double getMultinomialProbability(vocabDist emissionDist, vocabDist newDist) {
    double unigramProb = getUnigramProbability(emissionDist, newDist);
    double denomMultCoeff = 0;
    double numMultCoeff = 0;
    for (int i = 0; i < newDist.vocabWords.size(); i++) {
      int wordFreqInNewDist = newDist.vocabFreq.get(i);
      double fact = 1;
      for (int j = 1; j <= wordFreqInNewDist; j++) {
        fact *= j;
      }
      denomMultCoeff += Math.log10(fact);
    }
    for (int i = 1; i <= newDist.numTokens; i++) {
      numMultCoeff += Math.log10(i);
    }
    double multinomialCoeff = numMultCoeff - denomMultCoeff;
    return (multinomialCoeff + unigramProb);
  }

}

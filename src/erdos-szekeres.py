"""
https://code.google.com/codejam/contest/2442487/dashboard#s=p2

* Problem:
Given a list X, consisting of the numbers (1, 2, ..., N), an increasing subsequence is a subset of these numbers which appears in increasing order, and a decreasing subsequence is a subset of those numbers which appears in decreasing order. For example, (5, 7, 8) is an increasing subsequence of (4, 5, 3, 7, 6, 2, 8, 1).
Nearly 80 years ago, two mathematicians, Paul Erdos and George Szekeres proved a famous result: X is guaranteed to have either an increasing subsequence of length at least sqrt(N) or a decreasing subsequence of length of at least sqrt(N). For example, (4, 5, 3, 7, 6, 2, 8, 1) has a decreasing subsequence of length 4: (5, 3, 2, 1).
I am teaching a combinatorics class, and I want to "prove" this theorem to my class by example. For every number X[i] in the sequence, I will calculate two values:
    A[i]: The length of the longest increasing subsequence of X that includes X[i] as its largest number.
    B[i]: The length of the longest decreasing subsequence of X that includes X[i] as its largest number. 
The key part of my proof will be that the pair (A[i], B[i]) is different for every i, and this implies that either A[i] or B[i] must be at least sqrt(N) for some i. For the sequence listed above, here are all the values of A[i] and B[i]:
  i  |  X[i]  |  A[i]  |  B[i] 
-----+--------+--------+--------
  0  |   4    |   1    |   4
  1  |   5    |   2    |   4
  2  |   3    |   1    |   3
  3  |   7    |   3    |   4
  4  |   6    |   3    |   3
  5  |   2    |   1    |   2
  6  |   8    |   4    |   2
  7  |   1    |   1    |   1
I came up with a really interesting sequence to demonstrate this fact with, and I calculated A[i] and B[i] for every i, but then I forgot what my original sequence was. Given A[i] and B[i], can you help me reconstruct X?
X should consist of the numbers (1, 2, ..., N) in some order, and if there are multiple sequences possible, you should choose the one that is lexicographically smallest. This means that X[0] should be as small as possible, and if there are still multiple solutions, then X[1] should be as small as possible, and so on.

* Input:
The first line of the input gives the number of test cases, T. T test cases follow, each consisting of three lines.
The first line of each test case contains a single integer N. The second line contains N positive integers separated by spaces, representing A[0], A[1], ..., A[N-1]. The third line also contains N positive integers separated by spaces, representing B[0], B[1], ..., B[N-1].

* Output:
For each test case, output one line containing "Case #x: ", followed by X[0], X[1], ... X[N-1] in order, and separated by spaces.

* Limits:
1 <= T <= 30.
It is guaranteed that there is at least one possible solution for X.
- Small dataset:
1 <= N <= 20.
- Large dataset:
1 <= N <= 2000.

* Sample:
- Input:
2
1
1
1
8
1 2 1 3 3 1 4 1
4 4 3 4 3 2 2 1
- Output:
Case #1: 1
Case #2: 4 5 3 7 6 2 8 1
"""

from gcju import AbstractSolver, Runner


class Solver(AbstractSolver):
    """
    Approach:
    - Try to place the values in order from 1 to N.
    - For a value "n" to be placed at a position with A-value "a" and B-value "b":
      - a lower position with A-value "a-1" must already be filled
      - no lower positions with A-value equal to or greater than "a" should already be filled
      - a higher position with B-value "b-1" must already be filled
      - no higher positions with B-value equal to or greater than "b" should already be filled
    - The above means that an analysis of A and B allows us to limit the position range a value can be positioned at:
      - for A, we group the free candidate positions by increasing A-value, and for equal values we will consider higher positions first
      - for B, we group the free candidate positions by increasing B-value, and for equal values we will consider lower positions first
    - When trying to position a value:
      - we can restrict the initial position range to [first_candidate_from_A, first_candidate_from_B]
      - for each given A-value:
        - the only candidate is the one with the highest position
        - a candidate with A-value "a" and position "k" is only possible if a position lower than k with A-value "a-1" is filled
          and if "k" is in the range [highest_position_from_a_candidate_with_lower_A_value + 1, first_candidate_from_B]
      - for each given B-value:
        - the only candidate is the one with the lowest position
        - a candidate with B-value "b" and position "k" is only possible if a position higher than k with B-value "b-1" is filled
          and if "k" is in the range [first_candidate_from_B, lowest_position_from_a_candidate_with_lower_B_value - 1]
      - the set of possible positions is the intersection the candidates from A and the candidates from B
      - when there are several candidates, try the lowest position first as the problem statement says:
        "if there are multiple sequences possible, you should choose the one that is lexicographically smallest"
      - when there are no candidates, we need to roll back until a point where there are candidates left
    Note:
    - Without such a huge reduction of the possible position ranges the large data set cannot be solved in human time
      as the number of try-and-rollback passes explodes exponentially on some of the test cases.

    Example:
    - Let's take the sample case:
        8
        1 2 1 3 3 1 4 1
        4 4 3 4 3 2 2 1
    - Analysis of A:
        value 1: free indexes 0, 2, 5, 7 ; no filled indexes
        value 2: free indexes 1          ; no filled indexes
        value 3: free indexes 3, 4       ; no filled indexes
        value 4: free indexes 6          ; no filled indexes
    - Analysis of B:
        value 1: free indexes 7          ; no filled indexes
        value 2: free indexes 6, 5       ; no filled indexes
        value 3: free indexes 4, 2       ; no filled indexes
        value 4: free indexes 3, 1, 0    ; no filled indexes
    - Try to position 1:
      - initial index range [7, 7]
      - only one candidate position: index 7
    - Updated A structure:
        value 1: free indexes 0, 2, 5    ; filled indexes 7
        value 2: free indexes 1          ; no filled indexes
        value 3: free indexes 3, 4       ; no filled indexes
        value 4: free indexes 6          ; no filled indexes
    - Updated B structure:
        value 1: no free indexes         ; filled indexes 7
        value 2: free indexes 6, 5       ; no filled indexes
        value 3: free indexes 4, 2       ; no filled indexes
        value 4: free indexes 3, 1, 0    ; no filled indexes
    - Try to position 2:
      - initial index range [5, 5]
      - only one candidate position: index 5
    - Updated A structure:
        value 1: free indexes 0, 2       ; filled indexes 7, 5
        value 2: free indexes 1          ; no filled indexes
        value 3: free indexes 3, 4       ; no filled indexes
        value 4: free indexes 6          ; no filled indexes
    - Updated B structure:
        value 1: no free indexes         ; filled indexes 7
        value 2: free indexes 6          ; filled indexes 5
        value 3: free indexes 4, 2       ; no filled indexes
        value 4: free indexes 3, 1, 0    ; no filled indexes
    - Try to position 3:
      - initial index range [2, 6]
      - candidates for A:
        - value 1: 2
        - new range for A: [5, 6]
        - value 2: none
        - new range for A: empty
        => candidate positions for A: index 2
      - candidates for B:
        - value 2: 6
        - new range for B: [2, 5]
        - value 3: 2
        - new range for B: empty
        => candidate positions for B: indexes 6, 2
      - common candidate positions: index 2
    - Updated A structure:
        value 1: free indexes 0          ; filled indexes 7, 5, 2
        value 2: free indexes 1          ; no filled indexes
        value 3: free indexes 3, 4       ; no filled indexes
        value 4: free indexes 6          ; no filled indexes
    - Updated B structure:
        value 1: no free indexes         ; filled indexes 7
        value 2: free indexes 6          ; filled indexes 5
        value 3: free indexes 4          ; filled indexes 2
        value 4: free indexes 3, 1, 0    ; no filled indexes
    - Try to position 4:
      - initial index range [0, 6]
      - candidates for A:
        - value 1: 0
        - new range for A: [2, 6]
        - value 2: none
        - new range for A: empty
        => candidate positions for A: index 0
      - candidates for B:
        - value 2: 6
        - new range for B: [0, 5]
        - value 3: 4
        - new range for B: [0, 2]
        - value 4: 0
        => candidate positions for B: indexes 6, 4, 2
      - common candidate positions: index 0
    - Updated A structure:
        value 1: no free indexes         ; filled indexes 7, 5, 2, 0
        value 2: free indexes 1          ; no filled indexes
        value 3: free indexes 3, 4       ; no filled indexes
        value 4: free indexes 6          ; no filled indexes
    - Updated B structure:
        value 1: no free indexes         ; filled indexes 7
        value 2: free indexes 6          ; filled indexes 5
        value 3: free indexes 4          ; filled indexes 2
        value 4: free indexes 3, 1       ; filled indexes 0
    - Try to position 5:
      - initial index range [1, 6]
      - candidates for A:
        - value 2: 1
        - new range for A: empty
        => candidate positions for A: index 1
      - candidates for B:
        - value 2: 6
        - new range for B: [1, 5]
        - value 3: 4
        - new range for B: [1, 2]
        - value 4: 1
        => candidate positions for B: indexes 6, 4, 1
      - common candidate positions: index 1
    - Updated A structure:
        value 1: no free indexes         ; filled indexes 7, 5, 2, 0
        value 2: no free indexes         ; filled indexes 1
        value 3: free indexes 3, 4       ; no filled indexes
        value 4: free indexes 6          ; no filled indexes
    - Updated B structure:
        value 1: no free indexes         ; filled indexes 7
        value 2: free indexes 6          ; filled indexes 5
        value 3: free indexes 4          ; filled indexes 2
        value 4: free indexes 3          ; filled indexes 0, 1
    - Try to position 6:
      - initial index range [4, 6]
      - candidates for A:
        - value 3: 4
        - new range for A: empty
        => candidate positions for A: index 4
      - candidates for B:
        - value 2: 6
        - new range for B: [4, 5]
        - value 3: 4
        - new range for B: empty
        => candidate positions for B: indexes 6, 4
      - common candidate positions: index 4
    - Updated A structure:
        value 1: no free indexes         ; filled indexes 7, 5, 2, 0
        value 2: no free indexes         ; filled indexes 1
        value 3: free indexes 3          ; filled indexes 4
        value 4: free indexes 6          ; no filled indexes
    - Updated B structure:
        value 1: no free indexes         ; filled indexes 7
        value 2: free indexes 6          ; filled indexes 5
        value 3: no free indexes         ; filled indexes 2, 4
        value 4: free indexes 3          ; filled indexes 0, 1
    - Try to position 7:
      - initial index range [3, 6]
      - candidates for A:
        - value 3: 3
        - new range for A: [4, 6]
        - value 4: 6
        => candidate positions for A: indexes 3, 6
      - candidates for B:
        - value 2: 6
        - new range for B: [3, 5]
        - value 3: none
        - new range for B: [3, 4]
        - value 4: 3
        => candidate positions for B: indexes 6, 3
      - common candidate positions: indexes 3, 6
      - try index 3
    - Updated A structure:
        value 1: no free indexes         ; filled indexes 7, 5, 2, 0
        value 2: no free indexes         ; filled indexes 1
        value 3: no free indexes         ; filled indexes 4, 3
        value 4: free indexes 6          ; no filled indexes
    - Updated B structure:
        value 1: no free indexes         ; filled indexes 7
        value 2: free indexes 6          ; filled indexes 5
        value 3: no free indexes         ; filled indexes 2, 4
        value 4: no free indexes         ; filled indexes 0, 1, 3
    - Try to position 8:
      - initial index range [6, 6]
      - only one candidate position: index 6
    """

    def solveTestCase(self, inFh, outFh):

        # Read the input parameters
        # NOTE: For A and B we use the provided values minus one so that values are 0-based
        self._N = int(inFh.readline().strip())
        self._A = [int(val.strip()) - 1 for val in inFh.readline().split()]
        self._B = [int(val.strip()) - 1 for val in inFh.readline().split()]

        # Mark the start of processing
        self._startProcessing()

        # Solve
        self._prepare()
        while self._val <= self._N:
            if not self._findMatch():
                self._rollback()

        # Mark the end of processing
        self._endProcessing()

        # Write the output
        outFh.write(" ".join(str(val) for val in self._solution))

    def _prepare(self):

        self._aValsRemaining = [[] for _ in xrange(self._N)]
        self._aValsProcessed = [[] for _ in xrange(self._N)]
        for idx in xrange(self._N):
            a = self._A[idx]
            self._aValsRemaining[a].append(idx)
        self._aVal = 0

        self._bValsRemaining = [[] for _ in xrange(self._N)]
        self._bValsProcessed = [[] for _ in xrange(self._N)]
        for idx in xrange(self._N - 1, -1, -1):
            b = self._B[idx]
            self._bValsRemaining[b].append(idx)
        self._bVal = 0

        self._solution = [0] * self._N
        self._val = 1
        self._stack = []

    def _findMatch(self):

        # Identify the valid index range
        while not self._aValsRemaining[self._aVal]:
            self._aVal += 1
        while not self._bValsRemaining[self._bVal]:
            self._bVal += 1
        minIdx = self._aValsRemaining[self._aVal][-1]
        maxIdx = self._bValsRemaining[self._bVal][-1]
        # Identify the candidates from A
        aCandidates = set()
        aMinIdx = minIdx
        for a in xrange(self._aVal, self._N):
            if not self._aValsRemaining[a]:
                continue
            aCandidate = self._aValsRemaining[a][-1]
            if aMinIdx <= aCandidate <= maxIdx:
                aCandidates.add(aCandidate)
                aMinIdx = aCandidate + 1
                if aMinIdx > maxIdx:
                    break
            if not self._aValsProcessed[a]:
                break
            aProcessedIdx = self._aValsProcessed[a][-1]
            if aProcessedIdx > aMinIdx:
                aMinIdx = aProcessedIdx + 1
                if aMinIdx > maxIdx:
                    break
        # Identify the candidates from B
        bCandidates = set()
        bMaxIdx = maxIdx
        for b in xrange(self._bVal, self._N):
            if not self._bValsRemaining[b]:
                continue
            bCandidate = self._bValsRemaining[b][-1]
            if minIdx <= bCandidate <= bMaxIdx:
                bCandidates.add(bCandidate)
                bMaxIdx = bCandidate - 1
                if bMaxIdx < minIdx:
                    break
            if not self._bValsProcessed[b]:
                break
            bProcessedIdx = self._bValsProcessed[b][-1]
            if bProcessedIdx < bMaxIdx:
                bMaxIdx = bProcessedIdx - 1
                if bMaxIdx < minIdx:
                    break
        # Common candidates (in reverse order, to be able to pop in increasing order)
        matches = sorted(aCandidates & bCandidates, lambda x, y: cmp(y, x))
        if not matches:
            return False
        # Apply the first match
        self._stack.append(matches)
        idx = matches[-1]
        self._solution[idx] = self._val
        self._val += 1
        a = self._A[idx]
        self._aValsProcessed[a].append(self._aValsRemaining[a].pop())
        b = self._B[idx]
        self._bValsProcessed[b].append(self._bValsRemaining[b].pop())
        return True

    def _rollback(self):

        # Go back as much as necessary to find an alternate match
        while True:
            matches = self._stack.pop()
            previousIdx = matches.pop()
            self._solution[previousIdx] = 0
            self._val -= 1
            previousA = self._A[previousIdx]
            self._aValsRemaining[previousA].append(self._aValsProcessed[previousA].pop())
            previousB = self._B[previousIdx]
            self._bValsRemaining[previousB].append(self._bValsProcessed[previousB].pop())
            if previousA < self._aVal:
                self._aVal = previousA
            if previousB < self._bVal:
                self._bVal = previousB
            if matches:
                break
        # Apply the alternate match
        self._stack.append(matches)
        idx = matches[-1]
        self._solution[idx] = self._val
        self._val += 1
        a = self._A[idx]
        self._aValsProcessed.append(self._aValsRemaining[a].pop())
        b = self._B[idx]
        self._bValsProcessed.append(self._bValsRemaining[b].pop())


if __name__ == "__main__":
    solver = Solver()
    Runner.runFromCmdLine(solver)


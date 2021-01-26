Test Specification
==================

The tests specification have a state-action-state format. More specifically, the test specification shall follow this template:


.. code-block:: rst

  Given: (Screen X is visible) & (Explicit assertion A holds)

  When : Perform action A on control C (number n) with data D

  Wait: no/short/normal/long

  Then : (Screen Y is visible) & (Explicit Assertion B holds)

  Post : chaining test specifications (not compulsory)

Typeset Sphinx documentation!
==========================================

The framework would be primarily used by product-owners to specify (and test) different scenarios that may arise during the usage of the product. Such specifications would be referred to “test-scenarios” henceforth. An example of such a test is shown in Snippet 1.


.. code-block:: rst

  Given: the user is logged in and is at the Documents page
  When: the user clicks on the Formats Gallery
  Then: the Format Gallery page opens and 1 or more formats are displayed

**Snippet** 1

It is worthwhile to know that product-owners (i.e. that framework’s intended users) may or may not be well versed with programming languages. Therefore, it is imperative that the language for specifying the test-scenarios be intuitive,  free from technical jargon. Additionally, it must be expressive enough to be able to represent all possible behaviors of the product.

Furthermore, it is important to know that the product-owners may choose to specify partial test scenarios or end-to-end test scenarios. An end-to-end test-scenario implies a scenario that begins at the starting state of the product (`Typeset Homepage <https://typeset.io>`_
). Whereas a partial test-scenario need not not being at the starting state of the product. Given a product owner provides a partial test-scenario, it is the framework’s responsibility to automatically generate the missing pieces from the from the partial test-scenarios and subsequently generate end-to-end test scenarios.

The end-to-end test scenarios generated or received by the framework (in the framework’ specific language) would then be converted into machine readable test-cases (for ex. using selenium webdrivers). These machine-readable test cases are then executed by the framework. The execution of the test-cases may happen in a synchronous or asynchronous manner.  The artifacts generated from the test-execution, such as test reports, log files, etc, would be aggregated and presented for review by the framework.
  

Key benefits
~~~~~~~~~~~~

Key benefits of the Typeset Sphinx framework are listed down in the following:

1. It works for both single-page and multi-page applications (as compared to page-object-model based frameworks)

2. Provides high degree of automation - automatically generates models, test cases

3. Provides high level of abstraction - users not well versed with programming languages can also provide test specification

4. Provides high level of maintainability - test specifications remain relevant, even if low-level features such as Xpaths or CSSselector change over time



This documentation makes the most sense when read in this order
---------------------------------------------------------------

.. toctree::
   :maxdepth: 2
   :caption: Contents:

   overview
   language
   test-spec
   error

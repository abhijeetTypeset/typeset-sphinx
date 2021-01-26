Model Specification
========================================

A product can be defined as a collection of controls, widgets, apps, screens and pages. Following constraints need to be satisfied while writing the product model.
  
The product model

1. Must be specified in a valid Yaml file.

2. May contains a set of controls, widgets, apps, screens and pages.

3. All controls, widgets, apps and screen must have a valid id, wherein id is map of locator-type (css-selector, xpath, id, etc) and a locator-address.

4. All pages must have a valid url.

5. All controls, widgets, apps, screens and pages in the configuration file must have a name.

6. All controls, widgets, apps, screens and pages must have unique identifier.

7. All controls also have an action associated with them. Valid actions are "click" and "type".

8. All controls can have only one action associated with them.

9. All controls must lead to a valid widget or an app or a screen or a page.

10. All widgets must have an action click.

11. If the action is defined as "type", default data for the action must also be provided.

12. Controls may not refer to any other controls, widgets, apps, screens or pages.

13. Widgets may only refer to controls.

14. Apps may only refer to controls and widgets.

15. Screens may only refer to controls, widgets and apps.

16. Pages may only refer to controls, widgets, apps and screens.

17. There must be one-and-only-one root page in the model configuration.

18. Controls, widgets, apps, screens or pages may have precondition define for them. 

### Assertions and Precondtions
Assertions are mechanisms by which the state of the system can be ascertained. In Typeset Sphinx, there can be two types of assertions:

1. Implicit Assertions

2. Explicit Assertions
  
Implicit assertions, as the name suggests, are implicitly associated with the entities of the model. In particular, `atPage` function is implicitly associated with all pages in the model. At any give point (in test execution), `atPage` function can be invoked (along with the page id) to verify if the execution is at the page whose id has been provided as an argument. The `canSee` assertion associated with entities controls, widgets, apps and screens and can be used to check if an entity is visible at any given point in execution. In addition, controls that have the action "type" associated with them have the following implicit assertions associated with them:

* `contains`

* `startsWith`

* `endsWith`

* `equals`

* `empty`
  
The function type for `atPage` and `canSee` is `F:id --> boolean`, whereas, the function type for `contains`, `startsWith`, `endsWith`, `equals` and `empty` is `G:id,text --> boolean`.
  
Assertions, both implicit and explicit, will always evaluate to a boolean value.
   
Explicit assertions are composed of implicit assertions. In particular, all explicit assertions are `CNF Formula <https://en.wikipedia.org/wiki/Conjunctive_normal_form>`_. It can be further said that explicit assertions are conjunction of clauses. Where clauses can be said to be a disjunction of literals. Where literals are implicit assertions and their negations. While defining clauses in an explicit assertion, a number of constraints exist. They are:

* a clause can not be empty

* a clause can not contain a literal and its negation

* a clause should not contain duplicate literals

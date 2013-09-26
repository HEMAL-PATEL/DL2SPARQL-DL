![alt text](https://raw.github.com/adrielcafe/DL2SPARQL-DL/master/dl2sparqldl.png "DL2SPARQL-DL")

**DL2SPARQL-DL is an Java API that converts DL queries into SPARQL-DL queries**

### How it works
DL2SPARQL-DL receives a conjunction (⊓) of axioms in DL, separates them into atoms (via conjunctions) and performs the parsing of each element for the SPARQL-DL Query Patterns. 

The API takes the same alphabet used in DL (⊓, ∈, ≡, ⊆, ¬, +, -). Some variations as ʌ, v and = are not considered.

All elements TBox / Rbox / ABox in the query can be variables. To do this simply put a *?* at the beginning of the element,, eg, ?ClassB, ?RoleA, {?InstanceA}.

### Supported Query Patterns
| SPARQL-DL Query Patterns | DL Query Atoms             |
| ------------------------ | -------------------------- |
| Class(a)                 | ClassA                     |
| Property(a)              | roleA                      |
| Individual(a)            | {instanceA}                |
| Type(a, b)               | ClassA ∈ ClassB            |
| PropertyValue(a, b, c)   | roleA(classA, classB)      |
| EquivalentClass(a, b)    | ClassA ≡ ClassB            |
| SubClassOf(a, b)         | ClassA ⊆ ClassB            |
| EquivalentProperty(a, b) | roleA ≡ roleB              |
| SubPropertyOf(a, b)      | roleA ⊆ roleB              |
| InverseOf(a, b)          | roleA ≡ roleB-             |
| Transitive(a)            | roleA+                     |
| SameAs(a, b)             | {instanceA} ≡ {instanceB}  |
| DisjointWith(a, b)       | ClassA ⊆ ¬ClassB           |
| DifferentFrom(a, b)      | {instanceA} ⊆ ¬{instanceB} |

### Example
**Input**
```java
DL2SPARQLDL.parse(
  "?Wine ∈ Wine ∩ locatedIn(?Wine, ?Region)",
  "wine",
  "http://krono.act.uji.es/Links/ontologies/wine.owl#"
);
```

**Output**

```java
PREFIX wine: <http://krono.act.uji.es/Links/ontologies/wine.owl#>
SELECT ?Wine ?Region WHERE {
  Type(?Wine, wine:Wine),
  PropertyValue(?Wine, wine:locatedIn, ?Region)
}
```

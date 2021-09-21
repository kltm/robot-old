# Illegal Use of Built-In Vocabulary

**Problem:** Redefining built-in vocabulary should never be done.

**Solution:** Remove any statements about build-in vocabulary

```
PREFIX owl: <http://www.w3.org/2002/07/owl#>
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>

SELECT DISTINCT ?entity ?property ?value WHERE {
  VALUES ?entity {
      rdf:type
    }
  ?entity ?property ?value
}
ORDER BY ?entity
```
# Derby CASE expressions inside COALESCE

EclipseLink protects generated Derby `COALESCE` expressions whose first typed
argument contains a simple `CASE`, including inside a searched `CASE`, a scalar
function, a type-context wrapper or an explicit `CAST`.
Derby may evaluate that argument twice and fail while reusing the cached CASE
operand. For example:

```sql
COALESCE(CASE ID WHEN 2 THEN TITLE ELSE NULL END, 'fallback')
```

The Derby renderer adds an always-null argument whose type comes from the affected
CASE. Its condition is a literal false predicate, so the copied CASE is not evaluated:

```sql
COALESCE(CASE WHEN 1=0 THEN
    CASE ID WHEN 2 THEN TITLE ELSE NULL END
    ELSE NULL END,
    CASE ID WHEN 2 THEN TITLE ELSE NULL END, 'fallback')
```

The original arguments keep their order. Parameters in the copied CASE receive
corresponding binding entries before the original arguments. No SQL type-name,
length or precision table is introduced. Native SQL is not rewritten.

Leading bare parameters and bound literals are skipped only while binding is
stable. Automatic parameter-cast modes do not guarantee that every parameter is
cast, so a later CASE is conservatively protected in these modes too. A parameter
that does receive an implicit CAST may therefore receive the same harmless prefix
and conservative nullability metadata. A typed leading CAST without a contained
simple CASE, fields and unbound literals stop this search. The detector traverses
ordinary scalar-function and CASE children and their condition expressions; the
entire original wrapped receiver supplies the unreachable typed branch. Aggregate
and unclassified non-scalar operators, subqueries and nested COALESCE are boundaries;
nested COALESCE protects its own receiver. It does not traverse query roots or
relationship metadata, or infer function semantics from SQL names. Ordinary
COALESCE and searched-only CASE are unchanged. With dynamic SQL, a leading value can
already provide the typed receiver and retains its existing rendering.

Native investigation covers Derby 10.15.2.0, 10.16.1.1 and 10.17.1.0 on JDK 21
and 25, including scalar types, rebinding, nested CASE and evaluation counters.
The prefix preserves the tested result values, Java types, precision, scale and
display width. JDBC result nullability is conservative: an otherwise non-nullable
result can be reported as nullable. This is not a claim of identical metadata for
all Derby types, user-defined types, collations or LOBs.

This change does not upgrade Derby or require optional ELSE syntax. Explicit ELSE
remains usable independently of other JPQL changes. PostgreSQL TCK results measure
regression only; the repaired behavior is established by separate Derby provider
integration tests.

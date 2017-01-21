Changes from logic2j:

Term:
  nothing

Var:
  nothing

Struct:
- remove all handling of prolog lists and conversion of lists to java lists
- removed PrimitiveInfo
- no longer final
- Adding PrimitiveType, and adding the DATA value

TermApi:
- remove handling of prolog lists
- removed evaluate()
- removed
- removed normalization signature with LibraryContent
- valueOf: removed second argument FactoryMode (using always non ATOM)
- removed selectTerm()



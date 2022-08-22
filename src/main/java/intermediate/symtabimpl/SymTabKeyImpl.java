package intermediate.symtabimpl;

import intermediate.SymTabKey;

public enum SymTabKeyImpl implements SymTabKey {
  CONSTANT_VALUE,

  ROUTINE_CODE, ROUTINE_SYMTAB, ROUTINE_ICODE, ROUTINE_PARAMS, ROUTINE_ROUTINES,

  DATA_VALUE
}

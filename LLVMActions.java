import java.util.HashMap;
import java.util.Stack;

enum VarType{ INT, REAL, UNKNOWN }

class Value{ 
	public String name;
	public VarType type;
	public Value( String name, VarType type ){
		this.name = name;
		this.type = type;
	}
}

public class LLVMActions extends SLangBaseListener {
    
    HashMap<String, VarType> variables = new HashMap<String, VarType>();
    Stack<Value> stack = new Stack<Value>();

    @Override
    public void exitAssign(SLangParser.AssignContext ctx) { 
       String ID = ctx.ID().getText();
       Value v = stack.pop();
       variables.put(ID, v.type);
       if( v.type == VarType.INT ){
         LLVMGenerator.declare_i32(ID);
         LLVMGenerator.assign_i32(ID, v.name);
       } 
       if( v.type == VarType.REAL ){
         LLVMGenerator.declare_double(ID);
         LLVMGenerator.assign_double(ID, v.name);
       } 
    }

    @Override 
    public void exitProg(SLangParser.ProgContext ctx) { 
       System.out.println( LLVMGenerator.generate() );
    }
    @Override 
    public void exitValue(SLangParser.ValueContext ctx) { 
       if( ctx.ID() != null ){
         String ID = ctx.ID().getText();     
         if( variables.containsKey(ID) ) {
            LLVMGenerator.load( ID );
            stack.push( new Value("%"+(LLVMGenerator.reg-1), variables.get(ID)) ); 
         } else {
            error(ctx.getStart().getLine(), "unknown variable "+ID);         
         }
       } 
       if( ctx.INT() != null ){
         stack.push( new Value(ctx.INT().getText(), VarType.INT) );       
       }
       if( ctx.REAL() != null){
          stack.push( new Value(ctx.REAL().getText(), VarType.REAL) );
       } 
    }

    @Override 
    public void exitAdd(SLangParser.AddContext ctx) { 
       Value v1 = stack.pop();
       Value v2 = stack.pop();
       if( v1.type == v2.type ) {
         if( v1.type == VarType.INT ){
                  LLVMGenerator.add_i32(v1.name, v2.name); 
                  stack.push( new Value("%"+(LLVMGenerator.reg-1), VarType.INT) ); 
               }
         if( v1.type == VarType.REAL ){
                  LLVMGenerator.add_double(v1.name, v2.name); 
                  stack.push( new Value("%"+(LLVMGenerator.reg-1), VarType.REAL) ); 
               }
       } else {
          error(ctx.getStart().getLine(), "add type mismatch");
       }
    }

    @Override
    public void exitSub(SLangParser.SubContext ctx) {
      Value v1 = stack.pop();
      Value v2 = stack.pop();
      if( v1.type == v2.type ) {
         if( v1.type == VarType.INT ){
                  LLVMGenerator.sub_i32(v1.name, v2.name); 
                  stack.push( new Value("%"+(LLVMGenerator.reg-1), VarType.INT) ); 
               }
         if( v1.type == VarType.REAL ){
                  LLVMGenerator.sub_double(v1.name, v2.name); 
                  stack.push( new Value("%"+(LLVMGenerator.reg-1), VarType.REAL) ); 
               }
      } else {
         error(ctx.getStart().getLine(), "subtract type mismatch");
      }
   }
    
    @Override 
    public void exitMult(SLangParser.MultContext ctx) { 
       Value v1 = stack.pop();
       Value v2 = stack.pop();
       if( v1.type == v2.type ) {
	  if( v1.type == VarType.INT ){
             LLVMGenerator.mult_i32(v1.name, v2.name); 
             stack.push( new Value("%"+(LLVMGenerator.reg-1), VarType.INT) ); 
          }
	  if( v1.type == VarType.REAL ){
             LLVMGenerator.mult_double(v1.name, v2.name); 
             stack.push( new Value("%"+(LLVMGenerator.reg-1), VarType.REAL) ); 
         }
       } else {
          error(ctx.getStart().getLine(), "mult type mismatch");
       }
    }
    
    @Override
    public void exitDiv(SLangParser.DivContext ctx) {
      Value v1 = stack.pop();
      Value v2 = stack.pop();
      if( v1.type == v2.type ) {
    if( v1.type == VarType.INT ){
            LLVMGenerator.div_i32(v1.name, v2.name); 
            stack.push( new Value("%"+(LLVMGenerator.reg-1), VarType.INT) ); 
         }
    if( v1.type == VarType.REAL ){
            LLVMGenerator.div_double(v1.name, v2.name); 
            stack.push( new Value("%"+(LLVMGenerator.reg-1), VarType.REAL) ); 
        }
      } else {
         error(ctx.getStart().getLine(), "division type mismatch");
      }
    }

    @Override 
    public void exitToInt(SLangParser.ToIntContext ctx) { 
       Value v = stack.pop();
       LLVMGenerator.fptosi( v.name );
       stack.push( new Value("%"+(LLVMGenerator.reg-1), VarType.INT) ); 
    }

    @Override 
    public void exitToReal(SLangParser.ToRealContext ctx) { 
       Value v = stack.pop();
       LLVMGenerator.sitofp( v.name );
       stack.push( new Value("%"+(LLVMGenerator.reg-1), VarType.REAL) ); 
    }

    @Override
    public void exitPrint(SLangParser.PrintContext ctx) {
       String ID = ctx.ID().getText();
       VarType type = variables.get(ID);
       if( type != null ) {
          if( type == VarType.INT ){
            LLVMGenerator.printf_i32( ID );
          }
          if( type == VarType.REAL ){
            LLVMGenerator.printf_double( ID );
          }
       } else {
          error(ctx.getStart().getLine(), "unknown variable "+ID);
       }
    } 

   void error(int line, String msg){
       System.err.println("Error, line "+line+", "+msg);
       System.exit(1);
   } 
   
   @Override
   public void exitReadDouble(SLangParser.ReadDoubleContext ctx) {
      String ID = ctx.ID().getText();
      if( ! variables.containsKey(ID) ) {
          variables.put(ID, VarType.REAL);
          LLVMGenerator.declare_double(ID);          
       }
      LLVMGenerator.scanf_double(ID);
   }

   @Override
   public void exitReadInt(SLangParser.ReadIntContext ctx) {
      String ID = ctx.ID().getText();
      if( ! variables.containsKey(ID) ) {
         variables.put(ID, VarType.INT);
         LLVMGenerator.declare_i32(ID);          
      } 
      LLVMGenerator.scanf_i32(ID);

   }
}

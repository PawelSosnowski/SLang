import java.util.Stack;

class LLVMGenerator{
   
   static String header_text = "";
   static String main_text = "";
   static int main_reg = 1;
   static String buffer = "";
   static int reg = 1;
   static int br = 0;

   static Stack<Integer> brstack = new Stack<Integer>();

   static void close_main(){
      buffer += ";;;close_main\n";
      main_text += buffer;
   }

   static void functionstart(String id){
      buffer += ";;;functionstart\n";
      main_text += buffer;
      main_reg = reg;
      buffer = "define i32 @"+id+"() nounwind {\n";
      reg = 1;
   }

   static void functionend(){
      buffer += ";;;functionend\n";
      buffer += "ret i32 %"+(reg-1)+"\n";
      buffer += "}\n";
      header_text += buffer;
      buffer = "";
      reg = main_reg;
   }

   static void call(String id){
      buffer += ";;;call\n";
      buffer += "%"+reg+" = call i32 @"+id+"()\n";
      reg++;
   }

   static void printf_i32(String id){
      buffer += "%"+reg+" = load i32, i32* %"+id+"\n";
      reg++;
      buffer  += "%"+reg+" = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([4 x i8], [4 x i8]* @strpi, i32 0, i32 0), i32 %"+(reg-1)+")\n";
      reg++;
   }

   static void printf_double(String id){
      buffer  += "%"+reg+" = load double, double* %"+id+"\n";
      reg++;
      buffer  += "%"+reg+" = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([4 x i8], [4 x i8]* @strpd, i32 0, i32 0), double %"+(reg-1)+")\n";
      reg++;
   }

   static void scanf_i32(String id){
      buffer  += "%"+reg+" = call i32 (i8*, ...) @__isoc99_scanf(i8* getelementptr inbounds ([3 x i8], [3 x i8]* @strsi, i32 0, i32 0), i32* %"+id+")\n";
      reg++;
   }

   static void repeatstart(String repetitions){
      buffer  += ";;;for start\n";
      declare_i32(Integer.toString(reg));
      int counter = reg;
      reg++;
      assign_i32(Integer.toString(counter), "0");
      br++;
      buffer  += "br label %cond"+br+"\n";
      buffer  += "cond"+br+":\n";

      load_i32(Integer.toString(counter));
      add_i32("%"+Integer.toString(reg-1), "1");
      assign_i32(Integer.toString(counter), "%"+Integer.toString(reg-1));

      buffer  += "%"+reg+" = icmp slt i32 %"+(reg-2)+", "+repetitions+"\n";
      reg++;

      buffer  += "br i1 %"+(reg-1)+", label %true"+br+", label %false"+br+"\n";
      buffer  += "true"+br+":\n";
      brstack.push(br);
   }

   static void repeatend(){
      buffer  += ";;;for end\n";
      int b = brstack.pop();
      buffer  += "br label %cond"+b+"\n";
      buffer  += "false"+b+":\n";
   }

   static void ifstart(){
      br++;
      buffer += "br i1 %"+(reg-1)+", label %true"+br+", label %false"+br+"\n";
      buffer += "true"+br+":\n";
      brstack.push(br);
   }
 
   static void ifend(){
      int b = brstack.pop();
      buffer += "br label %false"+b+"\n";
      buffer += "false"+b+":\n";
   }

   static void icmp(String id, String value){
      buffer += "%"+reg+" = load i32, i32* %"+id+"\n";
      reg++;
      buffer += "%"+reg+" = icmp eq i32 %"+(reg-1)+", "+value+"\n";
      reg++;
   }

   static void incmp(String id, String value){
      buffer  += "%"+reg+" = load i32, i32* %"+id+"\n";
      reg++;
      buffer  += "%"+reg+" = icmp ne i32 %"+(reg-1)+", "+value+"\n";
      reg++;
   }
   
   static void scanf_double(String id){
      buffer  += "%"+reg+" = call i32 (i8*, ...) @__isoc99_scanf(i8* getelementptr inbounds ([4 x i8], [4 x i8]* @strsd, i64 0, i64 0), double* %"+id+")\n";
      reg++;
   }

   static void declare_i32(String id){
      buffer += "%"+id+" = alloca i32\n";
   }

   static void declare_double(String id){
      buffer += "%"+id+" = alloca double\n";
   }

   static void assign_i32(String id, String value){
      buffer += "store i32 "+value+", i32* %"+id+"\n";
   }

   static void assign_double(String id, String value){
      buffer += "store double "+value+", double* %"+id+"\n";
   }


   static void load_i32(String id){
      buffer += "%"+reg+" = load i32, i32* %"+id+"\n";
      reg++;
   }

   static void load_double(String id){
      buffer += "%"+reg+" = load double, double* %"+id+"\n";
      reg++;
   }

   static void add_i32(String val1, String val2){
      buffer += "%"+reg+" = add i32 "+val1+", "+val2+"\n";
      reg++;
   }

   static void add_double(String val1, String val2){
      buffer += "%"+reg+" = fadd double "+val1+", "+val2+"\n";
      reg++;
   }
   static void sub_i32(String val1, String val2){
      buffer += "%"+reg+" = sub i32 "+val2+", "+val1+"\n";
      reg++;
   }

   static void sub_double(String val1, String val2){
      buffer += "%"+reg+" = fsub double "+val2+", "+val1+"\n";
      reg++;
   }

   static void mult_i32(String val1, String val2){
      buffer += "%"+reg+" = mul i32 "+val1+", "+val2+"\n";
      reg++;
   }

   static void mult_double(String val1, String val2){
      buffer += "%"+reg+" = fmul double "+val1+", "+val2+"\n";
      reg++;
   }

   static void div_i32(String val1, String val2){
      buffer += "%"+reg+" = sdiv i32 "+val2+", "+val1+"\n";
      reg++;
   }

   static void div_double(String val1, String val2){
      buffer += "%"+reg+" = fdiv double "+val2+", "+val1+"\n";
      reg++;
   }


   static void sitofp(String id){
      buffer += "%"+reg+" = sitofp i32 "+id+" to double\n";
      reg++;
   }

   static void fptosi(String id){
      buffer += "%"+reg+" = fptosi double "+id+" to i32\n";
      reg++;
   }


   static String generate(){
      String text = "";
      text += "declare i32 @printf(i8*, ...)\n";
      text += "declare i32 @__isoc99_scanf(i8*, ...)\n";
      text += "@strpi = constant [4 x i8] c\"%d\\0A\\00\"\n";
      text += "@strpd = constant [4 x i8] c\"%f\\0A\\00\"\n";
      text += "@strsi = constant [3 x i8] c\"%d\\00\"\n";
      text += "@strsd = private unnamed_addr constant [4 x i8] c\"%lf\\00\", align 1\n";
      text += header_text;
      text += "define i32 @main() nounwind{\n";
      text += main_text;
      text += "ret i32 0 }\n";
      return text;
   }

}

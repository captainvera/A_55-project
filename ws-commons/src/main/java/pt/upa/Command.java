package pt.upa;

public abstract class Command {
  private String name, help;
  private Shell shell;

  public Command(Shell sh, String n) { this(sh, n, "<no help>"); }
  public Command(Shell sh, String n, String h) {
      name = n;
      help = h;
      (shell = sh).add(this);
  }
  /* package */ void help(String  h) { help = h; }

  public String name() { return name; }
  public String help() { return help; }
  public Shell shell() { return shell; }

  public abstract void execute(String[] args);

  public void print(String s) { shell.print(s); }
  public void println(String s) { shell.println(s); }
  public void flush() { shell.flush(); }
}

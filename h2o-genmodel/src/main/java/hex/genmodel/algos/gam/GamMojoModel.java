package hex.genmodel.algos.gam;

public class GamMojoModel extends GamMojoModelBase {
  String _link;
  double _tweedieLinkPower;
  private boolean _binomial;
  
  GamMojoModel(String[] columns, String[][] domains, String responseColumn) {
    super(columns, domains, responseColumn);
  }
  
  void init() { 
    _binomial = _family.equals("binomial");
  }
  
  @Override
  double[] gamScore0(double[] row, double[] preds) {
    return new double[0];
  }
}

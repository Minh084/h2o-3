package hex.genmodel.algos.gam;

import hex.genmodel.MojoModel;

public abstract class GamMojoModelBase extends MojoModel {
  boolean _useAllFactorLevels;
  int _cats;
  int[] _catNAFills;
  int _nums;
  double[] _numNAFills;
  boolean _meanImputation;
  double[] _beta;
  String _family;
  String[] _gam_columns;
  String[] _column_names;
  int[] _num_knots;
  double[][] _knots;
  double[][][] _binvD;
  double[][][] _zTranspose;
  
  GamMojoModelBase(String[] columns, String[][] domains, String responseColumn) {
    super(columns, domains, responseColumn);
  }
  
  @Override
  public double[] score0(double[] row, double[] preds) {
    if (_meanImputation)
      imputeMissingWithMeans(row);
    
    return gamScore0(row, preds);
  }
  
  void init() {;}
  
  abstract double[] gamScore0(double[] row, double[] preds);
  
  private void imputeMissingWithMeans(double[] data) {
    for (int ind=0; ind < _cats; ind++)
      if (Double.isNaN(data[ind])) data[ind] = _catNAFills[ind];
    for (int ind=0; ind < _nums; ind++)
      if (Double.isNaN(data[ind+_cats])) data[ind] = _numNAFills[ind];
  }
}

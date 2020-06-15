package hex.genmodel.algos.gam;

import hex.genmodel.ModelMojoReader;

import java.io.IOException;
import java.nio.ByteBuffer;

public class GamMojoReader extends ModelMojoReader<GamMojoModelBase> {

  @Override
  public String getModelName() {
    return "Generalized Additive Model";
  }

  @Override
  protected void readModelData() throws IOException {
    _model._useAllFactorLevels = readkv("use_all_factor_levels", false);
    _model._cats = readkv("cats", -1);
    _model._catNAFills = readkv("catNAFills", new int[0]);
    _model._numNAFills = readkv("numNAFills", new double[0]);
    _model._meanImputation = readkv("mean_imputation", false);
    _model._beta = readkv("beta");
    _model._family = readkv("family");
    // read in GAM specific parameters
    _model._num_knots = readkv("_num_knots");
    _model._gam_columns = readkv("_gam_columns");
    _model._column_names = readkv("_column_names");
    _model._knots = new double[_model._gam_columns.length][];
    _model._binvD = new double[_model._gam_columns.length][][];
    _model._zTranspose = new double[_model._gam_columns.length][][];
    _model._knots = read2DArrayDiffLength("knots", _model._knots, _model._num_knots);
    for (int gInd = 0; gInd < _model._gam_columns.length; gInd++) {
      _model._binvD[gInd] = new double[_model._num_knots.length-2][_model._num_knots.length];
      _model._binvD[gInd] = read2DArray(_model._gam_columns[gInd]+"_binvD", _model._binvD[gInd], 
              _model._binvD[gInd].length, _model._binvD[gInd][0].length);
      _model._zTranspose[gInd] = new double[_model._num_knots.length-1][_model._num_knots.length];
      _model._zTranspose[gInd] = read2DArray(_model._gam_columns[gInd]+"_zTranspose", _model._zTranspose[gInd],
              _model._zTranspose[gInd].length, _model._zTranspose[gInd][0].length);
    }
    
    if (_model instanceof GamMojoModel) {
      GamMojoModel m = (GamMojoModel) _model;
      m._link = readkv("link");
      m._tweedieLinkPower = readkv("tweedie_link_power", 0.0);
    }
    
    _model.init();
  }
  
  double[][] read2DArray(String title, double[][] row, int firstDSize, int secondDSize) throws IOException {
    row = new double[firstDSize][secondDSize];
    ByteBuffer bb = ByteBuffer.wrap(readblob(title));
    for (int i = 0; i < firstDSize; i++) {
      for (int j = 0; j < secondDSize; j++)
        row[i][j] = bb.getDouble();
    }
    return row;
  }
  
  double[][] read2DArrayDiffLength(String title, double[][] row, int[] num_knots) throws IOException {
    int numGamColumns = num_knots.length;
    ByteBuffer bb = ByteBuffer.wrap(readblob(title));
    for (int i = 0; i < numGamColumns; i++) {
      row[i] = new double[num_knots[i]];
      for (int j = 0; j < row.length; j++)
      row[i][j] = bb.getDouble();
    }
    return row;
  }

  @Override
  protected GamMojoModelBase makeModel(String[] columns, String[][] domains, String responseColumn) {
    return new GamMojoModel(columns, domains, responseColumn);
  }

  @Override
  public String mojoVersion() {
    return "1.00";
  }
}

import sys, os
sys.path.insert(1, "../../../")
import h2o
from tests import pyunit_utils
from random import randint

def gam_binomial_mojo():
    h2o.remove_all()
    NTESTROWS = 200    # number of test dataset rows
    PROBLEM="binomial"
    params = set_params()   # set deeplearning model parameters
    df = pyunit_utils.random_dataset(PROBLEM)   # generate random dataset
    dfnames = df.names
    # add GAM specific parameters
    params["gam_columns"] = []
    params["scale"] = []
    count = 0
    num_gam_cols = 3    # maximum number of gam columns
    for cname in dfnames:
        if not(cname == 'response') and (str(df.type(cname)) == "real"):
            params["gam_columns"].append(cname)
            params["scale"].append(0.001)
            count = count+1
            if (count >= num_gam_cols):
                break
    
    train = df[NTESTROWS:, :]
    test = df[:NTESTROWS, :]
    x = list(set(df.names) - {"response"})

    gamBinomialModel = pyunit_utils.build_save_model_GAM(params, x, train, "response") # build and save mojo model

    MOJONAME = pyunit_utils.getMojoName(gamBinomialModel._id)
    TMPDIR = os.path.normpath(os.path.join(os.path.dirname(os.path.realpath('__file__')), "..", "results", MOJONAME))

    h2o.download_csv(test[x], os.path.join(TMPDIR, 'in.csv'))  # save test file, h2o predict/mojo use same file
    pred_h2o, pred_mojo = pyunit_utils.mojo_predict(gamBinomialModel, TMPDIR, MOJONAME)  # load model and perform predict
    h2o.download_csv(pred_h2o, os.path.join(TMPDIR, "h2oPred.csv"))
    print("Comparing mojo predict and h2o predict...")
    pyunit_utils.compare_frames_local(pred_h2o, pred_mojo, 0.1, tol=1e-10)    # make sure operation sequence is preserved from Tomk        h2o.save_model(glmOrdinalModel, path=TMPDIR, force=True)  # save model for debugging

def set_params():
    missingValues = ['MeanImputation']
    missing_values = missingValues[randint(0, len(missingValues)-1)]

    params = {'missing_values_handling': missing_values, 'family':"binomial"}
    print(params)
    return params


if __name__ == "__main__":
    pyunit_utils.standalone_test(gam_binomial_mojo)
else:
    gam_binomial_mojo()

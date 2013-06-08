import math

from Numberjack import *
#from pylab import *
#from numpy import *
from scipy.optimize import leastsq

ec2_types = {
        'm1.small'       :{'cores':1 ,'compute_units':1   , 'cost':60}
        ,'m1.medium'     :{'cores':1 ,'compute_units':2   , 'cost':120}
        ,'m1.large'      :{'cores':2 ,'compute_units':4   , 'cost':240}
        ,'m1.xlarge'     :{'cores':4 ,'compute_units':8   , 'cost':480}
        ,'m3.xlarge'     :{'cores':4 ,'compute_units':13  , 'cost':500}
        ,'m3.2xlarge'    :{'cores':8 ,'compute_units':26  , 'cost':1000}
        ,'c1.medium'     :{'cores':2 ,'compute_units':5   , 'cost':145}
        ,'c1.xlarge'     :{'cores':8 ,'compute_units':20  , 'cost':580}
        ,'cc2.8xlarge'   :{'cores':32,'compute_units':88  , 'cost':2400}
        ,'m2.xlarge'     :{'cores':2 ,'compute_units':6.5 , 'cost':410}
        ,'m2.2xlarge'    :{'cores':4 ,'compute_units':13  , 'cost':820}
        ,'m2.4xlarge'    :{'cores':8 ,'compute_units':26  , 'cost':1640}
        ,'cr1.8xlarge'   :{'cores':32,'compute_units':88  , 'cost':3500}
        ,'hi1.4xlarge'   :{'cores':16,'compute_units':35  , 'cost':3100}
        ,'hs1.8xlarge'   :{'cores':16,'compute_units':35  , 'cost':4600}
        ,'t1.micro'      :{'cores':1 ,'compute_units':1   , 'cost':200000}
        ,'cg1.4xlarge'   :{'cores':16,'compute_units':33.5, 'cost':2100}
    }
    

def fit_variables():
    ## Parametric function: 'v' is the parameter vector, 'x' the independent varible
    fp = lambda v, x: v[0]/(x**v[1])*sin(v[2]*x)
    
    ## Noisy function (used to generate data to fit)
    v_real = [1.5, 0.1, 2.]
    fn = lambda x: fp(v_real, x)

    
    ## Error function
    e = lambda v, x, y: (fp(v,x)-y)
    
    ## Generating noisy data to fit
    n = 30
    xmin = 0.1
    xmax = 5
    x = linspace(xmin,xmax,n)
    print "x:%s" % x
    y = fn(x) + rand(len(x))*0.2*(fn(x).max()-fn(x).min())
    print "y:%s" % y
    
    ## Initial parameter value
    v0 = [3., 1, 4.]
    
    ## Fitting
    v, success = leastsq(e, v0, args=(x,y), maxfev=10000) 
    
    print "success: %s" % success
    print v
    
    print 'Estimater parameters: ', v
    print 'Real parameters: ', v_real
    X = linspace(xmin,xmax,n*5)
    plot(x,y,'ro', X, fp(v,X))
    
    show()


       

def adjust_dependent_var_for_type(baseval,val_per_core,val_per_cu,base,type):
    total = (val_per_core * (type['cores'] - base['cores'])) + (val_per_cu   * (type['compute_units'] - base['compute_units']))    
    return baseval + (total / 2)
    
def find_base(known,curr):
    for k in known:
        return k

def guess_dependent_var_vals(known,types):
    val_per_core = 0
    val_per_compute_unit = 0
    val_per_scaled_core = 0
        
    if len(known) == 1:
        for type in known:
            cores = types[type]['cores']
            compute_units = types[type]['compute_units']
            val = known[type]
            
            val_per_core += val/cores
            val_per_compute_unit += val/compute_units
    elif len(known) > 1:
        count = 0
        for type in known:
            other = [type2 for type2 in known if type2 != type]
            for type2 in other:
                count += 1
                cores1 = types[type]['cores']
                compute_units1 = types[type]['compute_units']
                
                cores2 = types[type2]['cores']
                compute_units2 = types[type2]['compute_units']
                
                val1 = known[type]
                val2 = known[type2]
                val = math.fabs(val1 - val2)
                dcore = math.fabs(cores1 - cores2)
                dcu = math.fabs(compute_units1 - compute_units2)
                
                core1_power = compute_units1 / cores1
                core2_power = compute_units2 / cores2
                
                core_scalar = core1_power / float(core2_power)
                
                scaled_cores1 = cores1 * core_scalar
                
                if dcore == 0:
                    val_per_core = 0
                else:
                    val_per_core += val/dcore
                    
                val_per_scaled_core += val/math.fabs(scaled_cores1 - cores2)
                
                if dcu == 0:
                    val_per_compute_unit = 0
                else:
                    val_per_compute_unit += val/dcu
                

                print "[%s] --> [%s]:" % (type,type2)
                print "             : val1:%f val2:%f" % (val1,val2)
                print "             : core1:%f core2:%f" % (cores1,cores2)
                print "             : ecu:%f ecu:%f" % (compute_units1,compute_units2)
                print "             : power1:%f power2:%f" % (core1_power,core2_power)
                print "             : per_core:%f per_cu:%f" % (val_per_core,val_per_compute_unit)
                print "             : scaled_cores1:%f core_scalar:%f" % (scaled_cores1,core_scalar)
                print "             : per_score:%f" % (val/math.fabs(scaled_cores1 - cores2))
            
        val_per_core = val_per_core / count
        val_per_compute_unit = val_per_compute_unit / count
    
    expected = {}
    i = 0
    for type in types:
        v = 0
        if type in known:
           v = known[type]
        else:
           base = find_base(known,types[type])
           baseval = known[base]
           v = adjust_dependent_var_for_type(baseval,
                                             val_per_core,
                                             val_per_compute_unit,
                                             types[base],
                                             types[type])
        expected[type] = v
    
    return expected



def deployment_opt(want_rps,splittable):

    model = Model()

    apps = ['web_server']

    
    # t1.micro - 32 @ 400u / 3000r
    # m3.xlarge - 287 @ 400u / 3000r
    # m1.large - 179 @ 400u / 3000r
    # m1.medium = 118 @ 400u / 3000r
    
    rps = {
       'web_server': guess_dependent_var_vals({#"t1.micro":32,
                                               "m3.xlarge":287,
                                               "m1.large":179,
                                               "m1.medium":118
                                               }, ec2_types)
    }

    min_rps = {
       'web_server':want_rps
    }
    
    rps_temp = {}
    for app in rps:
        print "Deriving expected rps for: %s" % app
        perf = []
        for type in ec2_types:
            expect = rps[app][type]
            print "    --expect: %f/rps on %s" % (expect,type)
            perf.append(int(expect))
        rps_temp[app] = perf
            
    rps = rps_temp
    #print rps
    
    ec2_cost = []
    for type in ec2_types:
        ec2_cost.append(ec2_types[type]['cost'])

    vm_instance_vars = []
    cost_vars = []

    for app in apps:
       if splittable:
           vars = [Variable(0,20) for type in ec2_types]
       else:
           vars = [Variable(0,1) for type in ec2_types]
           model.add(Sum(vars) == 1)
       vm_instance_vars.append(vars)
       model.add(Sum(vars,rps[app]) >= min_rps[app])
       cost = Variable(0,1000000)
       model.add(Sum(vars,ec2_cost) == cost)
       cost_vars.append(cost)

    model.add(Minimize(Sum(cost_vars)))

    solver = model.load("Mistral")
    solver.solve();

    print "#########################################"
    print "          Suggested Deployment"
    print "#########################################"

    i = 0
    for app in apps:
      vars = vm_instance_vars[i]
      j = 0
      for type in ec2_types:
         if vars[j].get_value() > 0:
             print "%s deployed to %s of %s" % (app,vars[j],type)
             #if(vars[j]):print "-- expect: %f/rps" % (vars[j].get_value() * rps[app][j])
         j = j + 1
      i = i + 1

#deployment_opt()
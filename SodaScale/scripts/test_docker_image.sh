# *******************     Helper Functions     ******************* #

usage () 
{
  help
  exit 127
}

help () 
{
  echo
  echo "Usage: $0 -ii input_image -ic input_command [-ip input_port] [ -li load_image -lc load_command [-lp load_port]]"
  echo
}

readArguments ()
{
  if [ $# -eq 0 ]; then usage; fi

  while [ "$1" != "" ] ; do
    case $1 in
      -ii )	input_image=$2 ; shift 2;;
      -ic )	input_command=$2 ; shift 2;;
      -ip )	input_port=$2 ; shift 2;;
      -li )	load_image=$2 ; shift 2;;
      -lc )	load_command=$2 ; shift 2;;
      -lp )	load_port=$2 ; shift 2;;
      -h  )	help ; exit ;;
        * )     echo invalid option $1; usage;; 
    esac
  done
}



# *******************        Main Function       ******************* #

run ()
{
# Get public IP of EC2 instance
ip=$(curl ipecho.net/plain)
ip=$(curl ipecho.net/plain)
ip=$(curl ipecho.net/plain)
# Yes, I meant to repeat it, because for some reason, it doesn't work the first time!
echo -----------------------------------------
echo

# Application
if [ "$input_port" == "" ]; then
  docker run -i -t $input_image $input_command 
else
  echo > docker run -i -t -p $input_port $input_image $input_command; echo
  docker run -i -t -p $input_port $input_image $input_command 
  port=$(docker port $(docker ps -l -q) "$input_port")
  echo ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
  echo Your application is now running at $ip:$port 
  echo -----------------------------------------
fi

# Load Generator
if [[ "$load_image" != "" && "$load_command" != "" ]]; then
  if [ "$load_port" == "" ]; then
    docker run -i -t $load_image $load_command $ip:$port
  else
  echo > docker run -i -t -p $load_port $load_image $load_command; echo
    docker run -i -t -p $load_port $load_image $load_command $ip:$port
    port=$(docker port $(docker ps -l -q) $load_port)
    echo ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
    echo Test results are accessible at $ip:$port 
    echo -----------------------------------------
  fi 
fi

}

run
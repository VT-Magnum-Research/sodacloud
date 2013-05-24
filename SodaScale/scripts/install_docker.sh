# *******************       Install Docker      ******************* #

installDocker ()
{
echo
echo 'Connected to Amazon EC2 instance'
echo -----------------------------------------
pkgs=$(dpkg -l)

if [[ ! $pkgs == *lxc-docker* ]]; then
  echo 'Installing Docker'
  echo
  sudo rm /var/lib/apt/lists/* -fr
  sudo sh -c "echo 'deb http://ppa.launchpad.net/dotcloud/lxc-docker/ubuntu precise main' >> /etc/apt/sources.list"
  sudo apt-get update
  sudo apt-get install --force-yes -y -qq linux-image-extra-`uname -r`
  sudo apt-get install --force-yes -y -qq lxc-docker
  echo
else
  echo 'Docker already installed'
fi
echo -----------------------------------------
}


installDocker
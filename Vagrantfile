# -*- mode: ruby -*-
# vi: set ft=ruby :

Vagrant.configure(2) do |config|
  config.vm.box = "ferventcoder/win7pro-x64-nocm-lite"
  config.vm.guest = :windows
  config.vm.communicator = "winrm"

  config.vm.provider "virtualbox" do |vb|
    vb.gui = true
    vb.memory = "2048"
  end
end

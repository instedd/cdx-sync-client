# -*- mode: ruby -*-
# vi: set ft=ruby :

Vagrant.configure(2) do |config|

  config.vm.define "windows" do |c|
    c.vm.box = "ferventcoder/win7pro-x64-nocm-lite"
    c.vm.guest = :windows
    c.vm.communicator = "winrm"
    c.vm.provision :shell, inline: 'NET USE V: \\\\VBOXSVR\\vagrant /PERSISTENT:YES'

    c.vm.provider "virtualbox" do |vb|
      vb.gui = true
      vb.memory = "2048"
    end
  end
end

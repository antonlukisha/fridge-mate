import configparser
import subprocess

config = configparser.ConfigParser()
config.read('dependencies.properties')

for package, version in config.items('DEFAULT'):
    subprocess.check_call(['pip', 'install', f"{package}=={version}"])

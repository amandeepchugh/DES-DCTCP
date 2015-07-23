for i in `seq 1 15`;
do
echo "Senders: $i"
sed -i "s|numSenders=NUMSENDER|numSenders=$i|" config.properties
java -classpath '.:../jar/*:' edu.cs681.simulator.SimulationController
sed -i "s|numSenders=$i|numSenders=NUMSENDER|" config.properties
echo "---------------------------------------------------------"
done;

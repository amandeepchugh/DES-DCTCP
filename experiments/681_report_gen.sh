#!/bin/bash
if [ "$#" -ne 1 ];
then
	echo "Usage: $0 <autoperf_capacity_analysis_file> " >&2;
	exit;
fi

input=$1
name=`echo $input | sed 's/.csv//'`
senders="-1"
on=true
echo "nusr,collectiveThroughput,averageQueueLength,packetDropProb,averageWaitingTime,averageWindowSize,timeoutProb" | tee -a "$name""_report.csv"
echo "##@@ 1" | tee -a "$name""_report.csv"
echo "################################## users range ############################" | tee -a "$name""_report.csv"
while read line
do
	if $on;
	then
		case "$line" in
		"Avg"*)
			windowsize=`echo $line | cut -d' ' -f 4`
			;;
		"Senders"*)
			if [ ! "$senders" = "-1" ];
			then
				echo "$senders,$tput,$qlen,$dropprob,$waitt,$windowsize,$timeout" | tee -a "$name""_report.csv"
			fi
			senders=`echo $line | cut -d' ' -f 2`
			;;
		"Collective"*)
			tput=`echo $line | cut -d' ' -f 5`
#			echo $loadlevel
			;;
		"Average Queue"*)
			qlen=`echo $line | cut -d' ' -f 4 `
#			echo $respt
			;;
		"Packet Drop"*)
			dropprob=`echo $line | cut -d' ' -f 5`
#			echo $tput;
			;;
		"Packet Timeouts"*)
			timeout=`echo $line | cut -d' ' -f 3`
			;;
		"Average Waiting"*)
			waitt=`echo $line | cut -d' ' -f 4`
#			echo $process
			;;
		esac
	fi
done < "$input"

			if [ ! "$senders" = "-1" ];
			then
				echo "$senders,$tput,$qlen,$dropprob,$waitt,$windowsize,$timeout" | tee -a "$name""_report.csv"
			fi

output="$name""_report.csv"
/home/bhavin/scripts/graph/single_plot.sh "`readlink -f "$output"`"

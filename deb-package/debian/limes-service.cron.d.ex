#
# Regular cron jobs for the limes-service package
#
0 4	* * *	root	[ -x /usr/bin/limes-service_maintenance ] && /usr/bin/limes-service_maintenance

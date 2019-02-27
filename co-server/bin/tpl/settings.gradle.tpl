
//{{prefix}}-{{service}}
include '{{prefix}}-{{service}}:{{prefix}}-{{service}}-api'
findProject(':{{prefix}}-{{service}}:{{prefix}}-{{service}}-api')?.name = '{{prefix}}-{{service}}-api'
include '{{prefix}}-{{service}}:{{prefix}}-{{service}}-service'
findProject(':{{prefix}}-{{service}}:{{prefix}}-{{service}}-service')?.name = '{{prefix}}-{{service}}-service'
include '{{prefix}}-{{service}}:{{prefix}}-{{service}}-dao'
findProject(':{{prefix}}-{{service}}:{{prefix}}-{{service}}-dao')?.name = '{{prefix}}-{{service}}-dao'
<?php

define('__ROOT__', realpath(dirname(__FILE__)) . DIRECTORY_SEPARATOR . "../../");

$command = new GenerateServiceCommand;
$command->init($argv);

class GenerateServiceCommand
{
    public function init($input)
    {
        if (!isset($input[1])) {
            $this->error("名称不能为空！");
        }

        $names = explode(":", $input[1]);
        if (count($names) == 2) {
            $name = $names[1];
            $service = strtolower($name);
            $groupname = strtolower($names[0]);
        } else {
            $this->error("格式为 {prefix}:{service} ");
        }

        if (!preg_match('/^[a-zA-Z\s]+$/', $name)) {
            $this->error("名称只能为英文！");
        }

        $mservice = "{$groupname}-{$service}";
        $this->outPut("开始初始化{$mservice}");

        $dir = __ROOT__."$mservice";
        $serviceDir = __ROOT__."$mservice/$mservice-service";
        $apiDir = __ROOT__."$mservice/$mservice-api";
        $daoDir = __ROOT__."$mservice/$mservice-dao";

        $this->outPut('正在生成目录...');

        if (is_dir($dir)) {
            $this->error('目录已存在...初始化失败');
        }

        mkdir($serviceDir."/src/main/java/{$groupname}/{$service}/service", 0755, true);
        mkdir($apiDir."/src/main/java/{$groupname}/{$service}/api", 0755, true);
        mkdir($daoDir."/src/main/java/{$groupname}/{$service}/dao", 0755, true);

        $this->outPut('开始创建模板...');
        $data = $this->getFile("build.gradle.tpl", $service, $groupname);
        file_put_contents ($dir."/build.gradle", $data);

        $data = $this->getFile("service.build.gradle.tpl", $service, $groupname);
        file_put_contents ($serviceDir."/build.gradle", $data);

        $data = $this->getFile("api.build.gradle.tpl", $service, $groupname);
        file_put_contents ($apiDir."/build.gradle", $data);


        $data = $this->getFile("Service.java.tpl", $service, $groupname);
        file_put_contents ($apiDir."/src/main/java/{$groupname}/{$service}/api/".ucfirst($service)."Service.java", $data);
        $data = $this->getFile("ServiceImpl.java.tpl", $service, $groupname);
        file_put_contents ($apiDir."/src/main/java/{$groupname}/{$service}/service/".ucfirst($service)."ServiceImpl.java", $data);
        $data = $this->getFile("ServiceProvider.java.tpl", $service, $groupname);
        file_put_contents ($dir."/src/main/java/ServiceProvider.java", $data);

        //更新settings.gradle
        $data = $this->getFile("settings.gradle.tpl", $service, $groupname);
        file_put_contents(__ROOT__."settings.gradle", $data, FILE_APPEND);
        $this->outPut("初始化{$mservice}完成");
    }

    private function getFile($tpl, $serviceName, $group)
    {
        $data = file_get_contents(__DIR__."/tpl/{$tpl}");

        return $this->getData($data, $serviceName, $group);
    }

    private function getData($data, $service, $prefix)
    {   
        $data = str_replace("{{prefix}}", $prefix, $data);
        $data = str_replace("{{service}}", $service, $data);
        return str_replace("{{Uservice}}", ucfirst($service), $data);
    }

    /**
     * 输出文本
     *
     */
    public function outPut($info)
    {
        echo $info.PHP_EOL;
    }

    /**
     * 错误提示
     *
     */
    public function error($error)
    {
        die($error.PHP_EOL);
    }
}

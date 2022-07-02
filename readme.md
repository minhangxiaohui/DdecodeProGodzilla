## 2022年hw中发现的红队魔改哥斯拉解码工具

## 一、魔改哥斯拉：

该款魔改哥斯拉的样本研究参见：https://xz.aliyun.com/t/11368

## 二、交互流量解码：

直接下载对应的jar包，DecoderProGodzilla.jar ， 或者把源码clone下来去打下包就行，其次就是getdata.py和jar放在同一个目录下，对是的，我懒嫌麻烦，就没用java去处理excel文件，而是拿py去做的。其中excel文件是通过ids相关设备导出的日志，提取魔改哥斯拉通信流量，生成对应的excel文件，对excel只有一个要求：导出流量里面对应请求体所在的列名为“数据(data)” 即可：
![image](https://user-images.githubusercontent.com/39674723/176987695-d94bcf99-8552-48ab-a454-40dd71c37931.png)

准备好对应的文件之后，运行实例如下：

![image](https://user-images.githubusercontent.com/39674723/176987700-8e5e38fe-1c4f-4fe6-89d2-7a1645c6858d.png)

![image](https://user-images.githubusercontent.com/39674723/176987706-9561af52-4b88-4322-a2e2-ac64cfbbbff8.png)

查看result解码结果：可以清楚的看到攻击者利用该webshell做了什么，从而开展追踪溯源工作

![image](https://user-images.githubusercontent.com/39674723/176987714-8876e922-9b41-4d9c-b365-a61e9dd3c7f0.png)

## 三、解码原理以及遇到的问题



解码原理，同样请参见c 该文，将文中的解码思路代码化即可，没什么难度。

接下来主要讲讲我们在解码过程中遇到的问题：

1、首先该工具暂时只支持解码请求流量，响应流量后续再看，因为请求流量解码遇到了点问题

2、解码请求流量的时候出现一个令人寻味的问题：

​	我发现客户端传给服务端的流量，在实际情况下并不是“全部都生效的”，为什么这么说呢，在解码过程中我发现红队手中的客户端发送到服务端的部分流量是畸形的，具体表现在于，解码环节中的uudecode上，当我使用uudecode去解码来自部分红队客户端的流量的时候，我发现会存在一些解不出来的流量。后来研究发现uuencode编码后的流量，每行的首字母都和该行对应的长度有关系，具体是什么关系其实就是个转换后长度，一般情况下，该行满了之后，行首位就是“M”，所以基本上行首都是“M”，很容易就能想到，最后一行是个特殊情况，可能不会满，那么这里就应该不是“M”，这样才能正确解码。但是在捕获的流量里面我发现，部分客户端请求过来的流量是uuencode的内容最后一行全部被打成了"M",所以在java uudecode中就会报错，长度错误之类的。就算我通过计算将其首位修正，最后在后面的还原中也会出现其他错误！

​	我分析这里有两种可能：

​	（1、红队武器库中的该工具其实还是没有完善的，存在一些可以优化的问题，由于一些bug，导致的部分请求流量无效（感觉这种可能性比较小）

​	（2、这部分流量是来迷惑，防守人员的，在此文https://xz.aliyun.com/t/11368 中，我们可以看到首先红队在请求流量里面使用了一些常见字段（toke、passwd、username等）来绕过一些监测人员。那有没有可能，这里也是故意参杂相关流量来绕过，webshell的研究人员，如果第一次研究的时候，研究拿到了一条解不出来的日志，然后研究人员被迷惑了之类的（感觉好像也可能性比较小）



## 四、优化

后续有时间的话，会、

- 把响应流量批量解码也补上。

- 还有就是优化掉里面的py

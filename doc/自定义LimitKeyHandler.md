# 自定义 LimitKeyHandle

可以通过自定义LimitKeyHandler处理器，根据自身系统特性，重写ip以及用户唯一主键的策略
```
@Component
public class myLimitKeyHandler implements LimitKeyHandler {
    @Override
    public String getUserKey() {
        return "hby";
    }

    @Override
    public String getIpKey() {
        return "10.82.111.111";
    }
}

```
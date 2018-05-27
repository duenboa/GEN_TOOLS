import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.Messages;

/**
 * 生成DTO
 */
public class GenDTOAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        String msg = "SELECT '$name'||atc.COLUMN_NAME||'$type'||atc.DATA_TYPE||'$scale'||atc.DATA_SCALE||'$cmt'||c.COMMENTS||'$end##########'" +
                "FROM all_tab_columns atc " +
                "JOIN all_col_comments c ON atc.TABLE_NAME = c.TABLE_NAME AND c.COLUMN_NAME = atc.COLUMN_NAME " +
                "WHERE c.TABLE_NAME = 'TABLE_NAME_大小写敏感';";
        Messages.showInfoMessage(msg,"1. 请根据下面SQL示例查询Oracle数据库, 获取对应表字段的元数据信息.)");
        String inputDialog = Messages.showInputDialog("请将上一步骤生成的元数据信息填写到输入框", "2. 数据准备", Messages.getInformationIcon());
        byte[] bytes = inputDialog.getBytes();
        String input = new String(bytes);
        //剔除换行符
        input = input.replace("\r\n","").replace("\n","");
        //2DTO
        String dtoString = Service.genDTO(input);
        //2CF
        String columnFieldString = Service.genFiledPropertyLine(input);
        Messages.showInfoMessage(dtoString + "\r\n" +columnFieldString,"3. DTO结果");
     }
}

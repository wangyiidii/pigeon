<!DOCTYPE html>
<html>
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <meta charset="utf-8">
    <title>${title}</title>
    <link href="https://cdn.staticfile.org/twitter-bootstrap/4.4.1/css/bootstrap.min.css" rel="stylesheet">

    <script src="https://cdn.bootcss.com/jquery/3.4.1/jquery.min.js"></script>
    <script src="https://cdn.staticfile.org/twitter-bootstrap/4.4.1/js/bootstrap.min.js"></script>
    <script src="https://cdn.bootcss.com/popper.js/1.16.1/umd/popper.min.js"></script>

</head>
<body>
<div class="container p-4">
    <div class="offset-md-3 col-md-6">
        <div style="border:1.5px solid #03a9f4;"></div>
        <div style="color:#555; border:1px solid #e9e9e9; box-shadow:0 1px 1px #aaa;">
            <div style="font-size:14px;" class="pt-4 pb-2 pl-2">
						<span style="color: #12ADDB">
							&gt;
						</span>
                ${mainHeader}
            </div>
            <div style="border-bottom:1px solid #e9e9e9;" class="m-2"></div>
            <div class="p-3">
                <div style="font-size:12px;color:#777;">
                    ${mainHeader}
                    <p style="background-color:#f5f5f5;padding:10px 15px;margin:18px 0;font-size: 14px;line-height: 25px">
                        CPU使用率：${cpuUtil}%<br/>
                        内存使用率：${memUtil}%<br/>
                    </p>
                </div>
            </div>
            <div style="color:#888;font-size:12px;background-color:#f5f5f5;" class="p-2">
                <p style="margin:0;padding:0;">
                    &#xA9;
                    <a style="color:#888;text-decoration:none;" href="https://yiidii.cn" title="" target="_blank">
                        yiidii
                    </a>
                    - 此邮件是自动生成的，请勿直接回复，如有疑问，请
                    <a style="text-decoration:none;color:#12ADDB;" href="mailto:3087233411@qq.com" target="_blank">
                        联系我
                    </a>。
                </p>
            </div>
        </div>
    </div>
</div>
</body>
</html>

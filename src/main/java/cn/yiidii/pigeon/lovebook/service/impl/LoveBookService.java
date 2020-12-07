package cn.yiidii.pigeon.lovebook.service.impl;

import cn.yiidii.pigeon.lovebook.entity.LoveBook;
import cn.yiidii.pigeon.lovebook.mapper.LoveBookMapper;
import cn.yiidii.pigeon.lovebook.service.ILoveBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author yiidii
 * @date 2020/3/26 23:23:29
 * @desc This class is used to ...
 */
@Service
public class LoveBookService implements ILoveBookService {

    @Autowired
    private LoveBookMapper loveBookMapper;

    @Override
    public List<LoveBook> queryAllLoveBook() {
        return loveBookMapper.queryAllLoveBook();
    }

    @Override
    public Integer insert(LoveBook loveBook) {
        return loveBookMapper.insert(loveBook);
    }
}

package com.staryet.baas.admin.controller;

import com.staryet.baas.admin.entity.App;
import com.staryet.baas.admin.entity.dto.AppDto;
import com.staryet.baas.admin.entity.dto.AppExport;
import com.staryet.baas.admin.service.AppService;
import com.staryet.baas.admin.service.StatService;
import com.staryet.baas.common.entity.SimpleResult;
import com.staryet.baas.object.service.ObjectService;
import com.staryet.baas.user.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedList;
import java.util.List;

import static com.staryet.baas.common.entity.SimpleResult.success;

/**
 * 应用控制器
 * Created by Staryet on 15/9/17.
 */
@RestController
@RequestMapping(value = "/api/admin/app")
public class AppController {

    @Autowired
    private AppService appService;
    @Autowired
    private StatService statService;
    @Autowired
    private ObjectService objectService;

    @RequestMapping(value = "", method = RequestMethod.POST)
    public SimpleResult insert(@RequestBody App app) {
        App newApp = appService.insert(app);
        SimpleResult result = success();
        result.putData("app", newApp);
        return result;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public SimpleResult delete(@PathVariable String id) {
        appService.delete(id);
        return success();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public App get(@PathVariable String id) {
        return appService.get(id);
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    @ResponseBody
    public List<AppDto> list() {
        List<App> apps = appService.list();
        LinkedList<AppDto> appDTOs = new LinkedList<>();
        apps.forEach(app -> {
            AppDto dto = new AppDto();
            BeanUtils.copyProperties(app, dto);
            appDTOs.add(dto);
            //计算用户总数
            dto.setUserCount(objectService.count(app.getId(), UserService.USER_CLASS_NAME, null, null, true));
            //请求数总和
            dto.setYesterday(statService.getYesterdayApiCount(app.getId()));
            dto.setCurrentMonth(statService.getCurrentMonthApiCount(app.getId()));
        });
        return appDTOs;
    }

    @RequestMapping(value = "/{id}/resetKey", method = RequestMethod.PUT)
    @ResponseBody
    public SimpleResult resetKey(@PathVariable String id) {
        appService.resetKey(id);
        return success();
    }

    @RequestMapping(value = "/{id}/resetMasterKey", method = RequestMethod.PUT)
    @ResponseBody
    public SimpleResult resetMasterKey(@PathVariable String id) {
        appService.resetMasterKey(id);
        return success();
    }

    @RequestMapping(value = "/{id}/export", method = RequestMethod.GET)
    @ResponseBody
    public AppExport export(@PathVariable String id) {
        return appService.export(id);
    }

    @RequestMapping(value = "/import", method = RequestMethod.POST)
    @ResponseBody
    public SimpleResult importData(@RequestBody AppExport appExport) {
        appService.importData(appExport);
        return success();
    }

}
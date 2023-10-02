import { AccessControl } from 'accesscontrol';

let grantsObject = {
    admin: {
        users: {
            'read:any': ['*'],
            'update:any': ['*', '!role'],
            'delete:any': ['*']
        }
    },
    user: {
        users: {
            'read:own': ['*'],
            'update:own': ['*', '!role'],
            'delete:own': ['*']
        }
    }
};

const acObject: AccessControl = new AccessControl(grantsObject);
acObject.lock();

export {acObject as ac};


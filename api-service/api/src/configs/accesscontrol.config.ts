import { AccessControl } from 'accesscontrol';

let grantsObject = {
    admin: {
        electionInfo: {
            'read:any': ['*'],
            'update:any': ['*'],
            'delete:any': ['*'],
            'create:any': ['*']
        },
        users: {
            'read:any': ['*'],
            'update:any': ['*', '!role'],
            'delete:any': ['*']
        },
        election: {
            'read:any': ['*'],
            'create:any': ['*'],
            'delete:any': ['*'],
        }
    },
    user: {
        users: {
            'read:own': ['*'],
            'update:own': ['*', '!role'],
            'delete:own': ['*']
        },
        election: {
            'read:any': ['*'],
            'update:any': ['*']
        }
    }
};

const acObject: AccessControl = new AccessControl(grantsObject);
acObject.lock();

export {acObject as ac};


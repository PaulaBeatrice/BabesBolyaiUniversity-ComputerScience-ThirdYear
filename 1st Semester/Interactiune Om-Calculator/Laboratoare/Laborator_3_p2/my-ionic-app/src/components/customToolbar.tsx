import React, { useContext, useState, useEffect } from 'react';
import { IonToolbar, IonTitle, IonButton, IonIcon } from '@ionic/react';
import { useNetwork } from '../use/useNetwork';
import { AuthContext } from '../auth/AuthProvider';
import { checkmarkCircle, closeCircle } from 'ionicons/icons';

interface CustomToolbarProps {
    title: string;
}

const CustomToolbar: React.FC<CustomToolbarProps> = ({ title }) => {
    const { logout } = useContext(AuthContext);
    const { networkStatus } = useNetwork();
    const [showWarning, setShowWarning] = useState(false);

    // useEffect(() => {
    //     if (networkStatus.status === 'danger') {
    //         setShowWarning(true);
    //     } else {
    //         setShowWarning(false);
    //     }
    // }, [networkStatus.status]);

    const containerStyle: React.CSSProperties = {
        display: 'flex',
        justifyContent: 'space-between',
        alignItems: 'center',
        width: '100%',
    };

    return (
        <IonToolbar>
           <div style={{ ...containerStyle, marginBottom: '20px' }}>
    <IonTitle>{title}</IonTitle>
    {networkStatus.connected ? (
        <IonIcon icon={checkmarkCircle} color='success' />
    ) : (
        <div style={{ display: 'flex', alignItems: 'left', color: 'red', textAlign: 'left', padding: '10px', position: 'fixed', bottom: '0', left: '0', width: '100%' }}>
            <IonIcon icon={closeCircle} color='danger' style={{ marginRight: '25px' }} />
            Changes will not be saved until the connection is restored.
        </div>
    )}
    <IonButton
        fill="outline"
        shape="round"
        color="light"
        slot="end"
        style={{ marginLeft: '10px', backgroundColor: 'black', color: 'white' }}
        onClick={logout}
        >
  Logout
</IonButton>
</div>

        </IonToolbar>
    );
};

export default CustomToolbar;




// import React, { useContext, useState, useEffect } from 'react';
// import { IonToolbar, IonTitle, IonButton, IonIcon, IonToast } from '@ionic/react';
// import { useNetwork } from '../use/useNetwork';
// import { AuthContext } from '../auth/AuthProvider';
// import { checkmarkCircle, closeCircle } from 'ionicons/icons';

// interface CustomToolbarProps {
//     title: string;
// }

// const CustomToolbar: React.FC<CustomToolbarProps> = ({ title }) => {
//     const { logout } = useContext(AuthContext);
//     const { networkStatus } = useNetwork();
//     const [showToast, setShowToast] = useState(false);

//     useEffect(() => {
//         if (networkStatus.status === 'danger') {
//             setShowToast(true);
//         } else {
//             setShowToast(false);
//         }
//     }, [networkStatus]);

//     const containerStyle: React.CSSProperties = {
//         display: 'flex',
//         justifyContent: 'space-between',
//         alignItems: 'center',
//         width: '100%',
//     };

//     return (
//         <IonToolbar>
//             <div style={containerStyle}>
//                 <IonTitle>{title}</IonTitle>
//                 {networkStatus.connected ? (
//                     <IonIcon icon={checkmarkCircle} color='success' />
//                 ) : (
//                     <IonIcon icon={closeCircle} color='danger' />
//                 )}
//                 <IonButton fill="outline" shape='round' color='dark' slot='end' style={{ marginLeft: '10px' }} onClick={logout}>Logout</IonButton>
//             </div>
//             <IonToast
//                 isOpen={showToast}
//                 onDidDismiss={() => setShowToast(false)}
//                 message="Changes will not be saved until the connection is restored."
//                 duration={2000} // Toast will be displayed for 2 seconds
//                 position="bottom"
//                 color="danger"
//             />
//         </IonToolbar>
//     );
// };

// export default CustomToolbar;

// // import React, { useContext } from 'react';
// // import { IonToolbar, IonTitle, IonButton, IonIcon } from '@ionic/react';
// // import { useNetwork } from '../use/useNetwork';
// // import { AuthContext } from '../auth/AuthProvider';
// // import { checkmarkCircle, closeCircle } from 'ionicons/icons';

// // interface CustomToolbarProps {
// //     title: string;
    
// // }

// // const CustomToolbar: React.FC<CustomToolbarProps> = ({ title }) => {
// //     const { logout } = useContext(AuthContext);
// //     const { networkStatus } = useNetwork();

// //     const containerStyle: React.CSSProperties = {
// //         display: 'flex',
// //         justifyContent: 'space-between',
// //         alignItems: 'center',
// //         width: '100%', 
// //     };
// //     return (
// //         <IonToolbar>
// //             <div style={containerStyle}>
// //                 <IonTitle >{title}</IonTitle>
// //                         {networkStatus.connected ? (
// //                                 <IonIcon icon={checkmarkCircle} color='success'/>
// //                             ) : (
// //                                 <IonIcon icon={closeCircle} color ='danger'/>

// //                             )}
                     
// //                         <IonButton fill="outline" shape='round' color='dark' slot='end' style={{marginLeft: '10px'}} onClick={logout} >Logout</IonButton>
                     
              
// //             </div>
// //         </IonToolbar >
// //     );
// // };

// // export default CustomToolbar;